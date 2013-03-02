/*
 * Phys2D - a 2D physics engine based on the work of Erin Catto. The
 * original source remains:
 * 
 * Copyright (c) 2006 Erin Catto http://www.gphysics.com
 * 
 * This source is provided under the terms of the BSD License.
 * 
 * Copyright (c) 2006, Phys2D
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 *  * Redistributions of source code must retain the above 
 *    copyright notice, this list of conditions and the 
 *    following disclaimer.
 *  * Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution.
 *  * Neither the name of the Phys2D/New Dawn Software nor the names of 
 *    its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 */
package net.phys2d.raw.collide;

import net.phys2d.math.MathUtil;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.shapes.Box;

/**
 * The implementation of box to box collision. The create() method is
 * used as a factory to produce the collider instance.
 * 
 * Currently the collider is stateless so a single instance is 
 * returned each time.
 * 
 * @author Kevin Glass
 */
public strictfp class BoxBoxCollider implements Collider {

//	 Box vertex and edge numbering:
	//
//	        ^ y
//	        |
//	        e1
//	   v2 ------ v1
//	    |        |
//	 e2 |        | e4  --> x
//	    |        |
//	   v3 ------ v4
//	        e3

	/** The identifier for the x coordinate of the first face */
	public static final int FACE_A_X = 1;
	/** The identifier for the y coordinate of the first face */
	public static final int FACE_A_Y = 2;
	/** The identifier for the x coordinate of the second face */
	public static final int FACE_B_X = 3;
	/** The identifier for the y coordinate of the second face */
	public static final int FACE_B_Y = 4;

	/** The identifier indicating no edges collided */
	public static final int NO_EDGE = 0;
	/** The identifier indicating the first edge collides */
	public static final int EDGE1 = 1;
	/** The identifier indicating the second edge collides */
	public static final int EDGE2 = 2;
	/** The identifier indicating the third edge collides */
	public static final int EDGE3 = 3;
	/** The identifier indicating the forth edge collides */
	public static final int EDGE4 = 4;

	/** Temp vector */
	private static Vector2f hA = new Vector2f();
	/** Temp vector */
	private static Vector2f hB = new Vector2f();
	
	/**
	 * A simple structure describe a vertex against which the
	 * shape should be clipped
	 * 
	 * @author Kevin Glass
	 */
	private class ClipVertex {
		/** The vertex */
		Vector2f v = new Vector2f();
		/** The pair this clipping applied to */
		FeaturePair fp = new FeaturePair();
		
		/**
		 * Create a new structure
		 */
		public ClipVertex() { 
		}
	};

	/** 
	 * Swap the two body edges within a feature pair over
	 * 
	 * @param fp The feature pair to flip
	 */
	private void flip(FeaturePair fp) {
		int temp = fp.inEdge1;
		fp.inEdge1 = fp.inEdge2;
		fp.inEdge2 = temp;
		
		temp = fp.outEdge1;
		fp.outEdge1 = fp.outEdge2;
		fp.outEdge2 = temp;
	}

	/**
	 * Clip a line segment against a line
	 * 
	 * @param vOut The segment to be clipped
	 * @param vIn The line to be clipped against
	 * @param normal The normal of the line
	 * @param offset The offset from segment to line 
	 * @param clipEdge The edge against which we're clipping
	 * @return The number of points we've clipped
	 */
	private int clipSegmentToLine(ClipVertex[] vOut, ClipVertex[] vIn,
						  Vector2f normal, float offset, char clipEdge) {
		// Start with no output points
		int numOut = 0;

		// Calculate the distance of end points to the line
		float distance0 = normal.dot(vIn[0].v) - offset;
		float distance1 = normal.dot(vIn[1].v) - offset;

		// If the points are behind the plane
		if (distance0 <= 0.0f) vOut[numOut++] = vIn[0];
		if (distance1 <= 0.0f) vOut[numOut++] = vIn[1];

		// If the points are on different sides of the plane
		if (distance0 * distance1 < 0.0f)
		{
			// Find intersection point of edge and plane
			float interp = distance0 / (distance0 - distance1);
			vOut[numOut].v = MathUtil.scale(MathUtil.sub(vIn[1].v, vIn[0].v),interp);
			vOut[numOut].v.add(vIn[0].v);
			
			if (distance0 > 0.0f)
			{
				vOut[numOut].fp = vIn[0].fp;
				vOut[numOut].fp.inEdge1 = clipEdge;
				vOut[numOut].fp.inEdge2 = NO_EDGE;
			}
			else
			{
				vOut[numOut].fp = vIn[1].fp;
				vOut[numOut].fp.outEdge1 = clipEdge;
				vOut[numOut].fp.outEdge2 = NO_EDGE;
			}
			++numOut;
		}

		return numOut;
	}

	/**
	 * ??
	 * 
	 * @param c
	 * @param h
	 * @param pos
	 * @param rot
	 * @param normal
	 */
	private void computeIncidentEdge(ClipVertex[] c, 
									ROVector2f h, 
									ROVector2f pos,
									Matrix2f rot, Vector2f normal) {
		// The normal is from the reference box. Convert it
		// to the incident boxe's frame and flip sign.
		Matrix2f rotT = rot.transpose();
		Vector2f n = MathUtil.scale(MathUtil.mul(rotT,normal),-1);
		Vector2f nAbs = MathUtil.abs(n);

		if (nAbs.x > nAbs.y)
		{
			if (MathUtil.sign(n.x) > 0.0f)
			{
				c[0].v.set(h.getX(), -h.getY());
				c[0].fp.inEdge2 = EDGE3;
				c[0].fp.outEdge2 = EDGE4;

				c[1].v.set(h.getX(), h.getY());
				c[1].fp.inEdge2 = EDGE4;
				c[1].fp.outEdge2 = EDGE1;
			}
			else
			{
				c[0].v.set(-h.getX(), h.getY());
				c[0].fp.inEdge2 = EDGE1;
				c[0].fp.outEdge2 = EDGE2;

				c[1].v.set(-h.getX(), -h.getY());
				c[1].fp.inEdge2 = EDGE2;
				c[1].fp.outEdge2 = EDGE3;
			}
		}
		else
		{
			if (MathUtil.sign(n.y) > 0.0f)
			{
				c[0].v.set(h.getX(), h.getY());
				c[0].fp.inEdge2 = EDGE4;
				c[0].fp.outEdge2 = EDGE1;

				c[1].v.set(-h.getX(), h.getY());
				c[1].fp.inEdge2 = EDGE1;
				c[1].fp.outEdge2 = EDGE2;
			}
			else
			{
				c[0].v.set(-h.getX(), -h.getY());
				c[0].fp.inEdge2 = EDGE2;
				c[0].fp.outEdge2 = EDGE3;

				c[1].v.set(h.getX(), -h.getY());
				c[1].fp.inEdge2 = EDGE3;
				c[1].fp.outEdge2 = EDGE4;
			}
		}

		c[0].v = MathUtil.mul(rot,c[0].v);
		c[0].v.add(pos);
		
		c[1].v = MathUtil.mul(rot,c[1].v);
		c[1].v.add(pos);
	}
	
	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		float x1 = bodyA.getPosition().getX();
		float y1 = bodyA.getPosition().getY();
		float x2 = bodyB.getPosition().getX();
		float y2 = bodyB.getPosition().getY();
		
		boolean touches = bodyA.getShape().getBounds().touches(x1,y1,bodyB.getShape().getBounds(),x2,y2);
		if (!touches) {
			return 0;
		}
		
		// Setup
		hA.set(((Box) bodyA.getShape()).getSize());
		hA.scale(0.5f);
		//Vector2f hA = MathUtil.scale(((Box) bodyA.getShape()).getSize(), 0.5f);
		hB.set(((Box) bodyB.getShape()).getSize());
		hB.scale(0.5f);
		//Vector2f hB = MathUtil.scale(((Box) bodyB.getShape()).getSize(), 0.5f);
		//Vector2f hA = MathUtil.scale(bodyA.getSize(), 0.5f);
		//Vector2f hB = MathUtil.scale(bodyB.getSize(), 0.5f);

		ROVector2f posA = bodyA.getPosition();
		ROVector2f posB = bodyB.getPosition();

		Matrix2f rotA = new Matrix2f(bodyA.getRotation());
		Matrix2f rotB = new Matrix2f(bodyB.getRotation());

		Matrix2f RotAT = rotA.transpose();
		Matrix2f RotBT = rotB.transpose();

		// unused?
//		Vector2f a1 = rotA.col1;
//		Vector2f a2 = rotA.col2;
//		Vector2f b1 = rotB.col1;
//		Vector2f b2 = rotB.col2;

		Vector2f dp = MathUtil.sub(posB,posA);
		Vector2f dA = MathUtil.mul(RotAT,dp);
		Vector2f dB = MathUtil.mul(RotBT,dp);

		Matrix2f C = MathUtil.mul(RotAT,rotB);
		Matrix2f absC = MathUtil.abs(C);
		Matrix2f absCT = absC.transpose();

		// Box A faces
		Vector2f faceA = MathUtil.abs(dA);
		faceA.sub(hA);
		faceA.sub(MathUtil.mul(absC,hB));
		
		if (faceA.x > 0.0f || faceA.y > 0.0f) {
			return 0;
		}

		// Box B faces
		Vector2f faceB = MathUtil.abs(dB);
		faceB.sub(MathUtil.mul(absCT,hA));
		faceB.sub(hB);
		//MathUtil.sub(MathUtil.sub(MathUtil.abs(dB),MathUtil.mul(absCT,hA)),hB);
		if (faceB.x > 0.0f || faceB.y > 0.0f) {
			return 0;
		}

		// Find best axis
		int axis;
		float separation;
		Vector2f normal;

		// Box A faces
		axis = FACE_A_X;
		separation = faceA.x;
		normal = dA.x > 0.0f ? rotA.col1 : MathUtil.scale(rotA.col1,-1);

		if (faceA.y > 1.05f * separation + 0.01f * hA.y)
		{
			axis = FACE_A_Y;
			separation = faceA.y;
			normal = dA.y > 0.0f ? rotA.col2 : MathUtil.scale(rotA.col2,-1);
		}

		// Box B faces
		if (faceB.x > 1.05f * separation + 0.01f * hB.x)
		{
			axis = FACE_B_X;
			separation = faceB.x;
			normal = dB.x > 0.0f ? rotB.col1 : MathUtil.scale(rotB.col1,-1);
		}

		if (faceB.y > 1.05f * separation + 0.01f * hB.y)
		{
			axis = FACE_B_Y;
			separation = faceB.y;
			normal = dB.y > 0.0f ? rotB.col2 : MathUtil.scale(rotB.col2,-1);
		}

		// Setup clipping plane data based on the separating axis
		Vector2f frontNormal, sideNormal;
		ClipVertex[] incidentEdge = new ClipVertex[] {new ClipVertex(), new ClipVertex()};
		float front, negSide, posSide;
		char negEdge, posEdge;

		// Compute the clipping lines and the line segment to be clipped.
		switch (axis)
		{
		case FACE_A_X:
			{
				frontNormal = normal;
				front = posA.dot(frontNormal) + hA.x;
				sideNormal = rotA.col2;
				float side = posA.dot(sideNormal);
				negSide = -side + hA.y;
				posSide =  side + hA.y;
				negEdge = EDGE3;
				posEdge = EDGE1;
				computeIncidentEdge(incidentEdge, hB, posB, rotB, frontNormal);
			}
			break;

		case FACE_A_Y:
			{
				frontNormal = normal;
				front = posA.dot(frontNormal) + hA.y;
				sideNormal = rotA.col1;
				float side = posA.dot(sideNormal);
				negSide = -side + hA.x;
				posSide =  side + hA.x;
				negEdge = EDGE2;
				posEdge = EDGE4;
				computeIncidentEdge(incidentEdge, hB, posB, rotB, frontNormal);
			}
			break;

		case FACE_B_X:
			{
				frontNormal = MathUtil.scale(normal,-1);
				front = posB.dot(frontNormal) + hB.x;
				sideNormal = rotB.col2;
				float side = posB.dot(sideNormal);
				negSide = -side + hB.y;
				posSide =  side + hB.y;
				negEdge = EDGE3;
				posEdge = EDGE1;
				computeIncidentEdge(incidentEdge, hA, posA, rotA, frontNormal);
			}
			break;

		case FACE_B_Y:
			{
				frontNormal = MathUtil.scale(normal,-1);
				front = posB.dot(frontNormal) + hB.y;
				sideNormal = rotB.col1;
				float side = posB.dot(sideNormal);
				negSide = -side + hB.x;
				posSide =  side + hB.x;
				negEdge = EDGE2;
				posEdge = EDGE4;
				computeIncidentEdge(incidentEdge, hA, posA, rotA, frontNormal);
			}
			break;
		default:
			throw new RuntimeException("Unknown face!");
		}

		// clip other face with 5 box planes (1 face plane, 4 edge planes)

		ClipVertex[] clipPoints1 = new ClipVertex[] {new ClipVertex(), new ClipVertex()};
		ClipVertex[] clipPoints2 = new ClipVertex[] {new ClipVertex(), new ClipVertex()};
		int np;

		// Clip to box side 1
		np = clipSegmentToLine(clipPoints1, incidentEdge, MathUtil.scale(sideNormal,-1), negSide, negEdge);

		if (np < 2)
			return 0;

		// Clip to negative box side 1
		np = clipSegmentToLine(clipPoints2, clipPoints1,  sideNormal, posSide, posEdge);

		if (np < 2)
			return 0;

		// Now clipPoints2 contains the clipping points.
		// Due to roundoff, it is possible that clipping removes all points.

		int numContacts = 0;
		for (int i = 0; i < 2; ++i)
		{
			float separation2 = frontNormal.dot(clipPoints2[i].v) - front;

			if (separation2 <= 0)
			{
				contacts[numContacts].setSeparation(separation2);
				contacts[numContacts].setNormal(normal);
				// slide contact point onto reference face (easy to cull)
				contacts[numContacts].setPosition(MathUtil.sub(clipPoints2[i].v,MathUtil.scale(frontNormal,separation2)));
				contacts[numContacts].setFeature(clipPoints2[i].fp);
				if (axis == FACE_B_X || axis == FACE_B_Y)
					flip(contacts[numContacts].getFeature());
				++numContacts;
			}
		}

		return numContacts;
	}
}
