/*
 * Phys2D - a 2D physics engine based on the work of Erin Catto.
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

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.*;

/**
 * A collider factory to create colliders for arbitrary bodies, 
 * or actually their shapes.
 * This was implemented to replace a visitor-pattern based implementation,
 * that required many files to be edited to add a new shape.
 * Furthermore this factory can handle singleton colliders if needed.
 * 
 * @author Gideon Smeding
 *
 */
public class ColliderFactory {
	
	/**
	 * Create a collider for two bodies. The decision depends on
	 * the body's shapes.
	 * 
	 * @param bodyA First body in the collision test
	 * @param bodyB Second body in the collision test
	 * @return A collider that can test wether the two bodies actually collide
	 * @throws ColliderUnavailableException 
	 *         This exception will be thrown if no suitable collider can be found. 
	 */
	public Collider createCollider(Body bodyA, Body bodyB) 
	throws ColliderUnavailableException {
		Shape shapeA = bodyA.getShape();
		Shape shapeB = bodyB.getShape();
		
		if ( shapeA instanceof Circle ) {
			return createColliderFor((Circle) shapeA, shapeB);
		} else if ( shapeA instanceof Box ) {
			return createColliderFor((Box) shapeA, shapeB);
		} else if ( shapeA instanceof Line ) {
			return createColliderFor((Line) shapeA, shapeB);
		} else if ( shapeA instanceof Polygon ) {
			return createColliderFor((Polygon) shapeA, shapeB);
		}
		
		throw new ColliderUnavailableException(shapeA, shapeB);
	}
	
	/**
	 * Creates a collider for a Circle and a Shape.
	 * The choice is based on the kind of Shape that is provided
	 * 
	 * @param shapeA The circle to provide a collider for
	 * @param shapeB The shape to provide a collider for
	 * @return a suitable collider
	 * @throws ColliderUnavailableException
	 * 	       This exception will be thrown if no suitable collider can be found.
	 */
	public Collider createColliderFor(Circle shapeA, Shape shapeB) 
	throws ColliderUnavailableException {

		if ( shapeB instanceof Circle ) {
			return new CircleCircleCollider();
		} else if ( shapeB instanceof Box ) {
			return new SwapCollider(new BoxCircleCollider());
		} else if ( shapeB instanceof Line ) {
			return new SwapCollider(new LineCircleCollider());
		} else if ( shapeB instanceof Polygon ) {
			return new SwapCollider(new PolygonCircleCollider());
		}
		
		throw new ColliderUnavailableException(shapeA, shapeB);
	}
	
	/**
	 * Creates a collider for a Box and a Shape.
	 * The choice is based on the kind of Shape that is provided
	 * 
	 * @param shapeA The box to provide a collider for
	 * @param shapeB The shape to provide a collider for
	 * @return a suitable collider
	 * @throws ColliderUnavailableException
	 * 	       This exception will be thrown if no suitable collider can be found.
	 */
	public Collider createColliderFor(Box shapeA, Shape shapeB) 
	throws ColliderUnavailableException {

		if ( shapeB instanceof Circle ) {
			return new BoxCircleCollider();
		} else if ( shapeB instanceof Box ) {
			return new BoxBoxCollider();
		} else if ( shapeB instanceof Line ) {
			return new SwapCollider(new LineBoxCollider());
		} else if ( shapeB instanceof Polygon ) {
			return new SwapCollider(new PolygonBoxCollider());
		}
		
		throw new ColliderUnavailableException(shapeA, shapeB);
	}
	
	/**
	 * Creates a collider for a Line and a Shape.
	 * The choice is based on the kind of Shape that is provided
	 * 
	 * @param shapeA The line to provide a collider for
	 * @param shapeB The shape to provide a collider for
	 * @return a suitable collider
	 * @throws ColliderUnavailableException
	 * 	       This exception will be thrown if no suitable collider can be found.
	 */
	public Collider createColliderFor(Line shapeA, Shape shapeB) 
	throws ColliderUnavailableException {

		if ( shapeB instanceof Circle ) {
			return new LineCircleCollider();
		} else if ( shapeB instanceof Box ) {
			return new LineBoxCollider();
		} else if ( shapeB instanceof Line ) {
			return new LineLineCollider();
		} else if ( shapeB instanceof Polygon ) {
			return new LinePolygonCollider();
		}
		
		throw new ColliderUnavailableException(shapeA, shapeB);
	}
	
	/**
	 * Creates a collider for a ConvexPolygon and a Shape.
	 * The choice is based on the kind of Shape that is provided
	 * 
	 * @param shapeA The convex polygon to provide a collider for
	 * @param shapeB The shape to provide a collider for
	 * @return a suitable collider
	 * @throws ColliderUnavailableException
	 * 	       This exception will be thrown if no suitable collider can be found.
	 */
	public Collider createColliderFor(Polygon shapeA, Shape shapeB) 
	throws ColliderUnavailableException {

		if ( shapeB instanceof Circle ) {
			return new PolygonCircleCollider();
		} else if ( shapeB instanceof Box ) {
			return new PolygonBoxCollider();
		} else if ( shapeB instanceof Line ) {
			return new SwapCollider(new LinePolygonCollider());
		} else if ( shapeB instanceof Polygon ) {
			return new PolygonPolygonCollider();
		}
		
		throw new ColliderUnavailableException(shapeA, shapeB);
	}
}
