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

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 * <p>Implements a sweepline algorithm that facilitates collision detection between
 * two polygons. For two polygons A and B it determines a set of collision candidates,
 *  i.e. the edges of A and B that can collide. </p>
 * 
 * <p>Getting a good approximation of the set of colliding edges is important
 * because given two polygons with n and m vertices each, checking every 
 * combination would take n*m operations.</p>
 * 
 * <p>To limit the number of candidates we project all edges of both polygons onto
 * a line. This line is the direction of the sweepline, the sweepline itself would
 * be perpendicular to it. The start and endpoints of the edges are sorted by their
 * projection onto the sweep direction.</p>
 * 
 * <p>The collision candidates can now be determined by walking through the list
 * of start- and endpoints of the edges and check which edges of A and B overlap.
 * If two edges do not overlap in the projection, they will not intersect,
 * therefore it is safe to discard the combination as a collision candidate.</p>
 * 
 * <p><i>Note that this approach is very similar to and indeed inspired by the
 * separating axes theorem.</i></p>
 * 
 * <p>The effectiveness of this algorithm depends on the choice of sweep direction.
 * For example using the line from one polygon's center to the other's center as
 * our sweep direction will give great results if both polygons are more or less 
 * round. However, when one of the polygons is very long, this will be (depending
 * on the polygon's positions) a very bad idea.<br>
 * The choice for the sweep direction is left to the user of this class.</p>
 * 
 * <h3>Insertion Sort</h3>
 * <p>For this sweepline algorithm there is a major assumption
 * regarding the complexity of the sorting. The used sorting algorithm uses the
 * fact that the edges will mostly be inserted in order of their position on the
 * sweepline (already sorted).</p>
 * 
 * <p>This seems justified by the observation that most polygons will be more or less
 * monotone in any direction, including the sweep direction. For convex polygons
 * this clearly holds, giving us a worst case complexity of O(n). Non-convex
 * polygons can however cause trouble with a worst case complexity of O(n*n).</p>   
 *  
 * 
 * @author Gideon Smeding
 *
 */
public class EdgeSweep {

	/** The doubly linked list list of inserted vertices */
	class ProjectedVertex {
		/** Vertex number, usually the index of the vertex in a polygon's array */
		public int vertex;
		/** True if this is a vertex belonging to polygon A, false if B */
		public boolean isA;
		/** Distance of the projection onto the sweep direction from the origin */
		public float distance;
		
		/** Next vertex in the list */
		public ProjectedVertex next;
		/** Next previous in the list */
		public ProjectedVertex previous;
	
		/** 
		 * Construct a list element with all it's values set except the next and
		 * previous elements of the list.
		 * 
		 * @param vertex Vertex number, usually the index of the vertex in a polygon's array
		 * @param isA True if this is a vertex belonging to polygon A, false if B
		 * @param distance Distance of the projection onto the sweep direction from the origin
		 */
		public ProjectedVertex(int vertex, boolean isA, float distance) {
			this.vertex = vertex;
			this.isA = isA;
			this.distance = distance;
		}
	}
	
	/** The last inserted element in the projected vertex list */
	private ProjectedVertex current;	
	
	/** The direction in which to sweep */
	private Vector2f sweepDir;
	
	/** Constructs an EdgeSweep object with the given sweep direction.
	 * 
	 * @param sweepDir The direction in which to sweep
	 */
	public EdgeSweep(ROVector2f sweepDir) {
		this.sweepDir = new Vector2f(sweepDir);
	}
	
	/**
	 * Insert a new element into our list that is known to be somewhere before 
	 * the current element. It walks backwards over the vertex list untill a vertex
	 * with a smaller distance or the start of the list is reached and inserts
	 * the element there.
	 * 
	 * @param vertex Vertex number, usually the index of the vertex in a polygon's array
	 * @param isA True if this is a vertex belonging to polygon A, false if B
	 * @param distance Distance of the projection onto the sweep direction from the origin
	 */
	private void insertBackwards(int vertex, boolean isA, float distance) {
		ProjectedVertex svl = new ProjectedVertex(vertex, isA, distance);
		
		if ( current == null ) {
			current = svl;
			return;
		}
		
		while ( current.distance > svl.distance ) {
			if ( current.previous == null ) {
				// insert before current
				current.previous = svl;
				svl.next = current;
				current = svl;
				return;
			}
			
			current = current.previous;
		}
		
		// insert after current
		svl.next = current.next;
		svl.previous = current;
		current.next = svl;
		
		if ( svl.next != null )
			svl.next.previous = svl;
			
		current = svl;
	}
	
	/**
	 * Insert a vertex into the sorted list.
	 * 
	 * @param vertex Vertex number, usually the index of the vertex in a polygon's array
	 * @param isA True if this is a vertex belonging to polygon A, false if B
	 * @param distance Distance of the projection onto the sweep direction from the origin
	 */
	public void insert(int vertex, boolean isA, float distance) {
		if ( current == null || current.distance <= distance )
			insertForwards(vertex, isA, distance);
		else
			insertBackwards(vertex, isA, distance);
	}
	
	/**
	 * Insert a new element into our list that is known to be somewhere after 
	 * the current element. It walks forwards over the vertex list untill a vertex
	 * with a smaller distance or the end of the list is reached and inserts
	 * the element there.
	 * 
	 * @param vertex Vertex number, usually the index of the vertex in a polygon's array
	 * @param isA True if this is a vertex belonging to polygon A, false if B
	 * @param distance Distance of the projection onto the sweep direction from the origin
	 */
	private void insertForwards(int vertex, boolean isA, float distance) {
		ProjectedVertex svl = new ProjectedVertex(vertex, isA, distance);
		
		if ( current == null ) {
			current = svl;
			return;
		}
		
		while ( current.distance <= svl.distance ) {
			if ( current.next == null ) {
				// insert after current
				current.next = svl;
				svl.previous = current;
				current = svl;
				return;
			}
			
			current = current.next;
		}
		
		// insert before current
		svl.next = current;
		svl.previous = current.previous;
		current.previous = svl;
		
		if ( svl.previous != null )
			svl.previous.next = svl;
			
		current = svl;
	}
	
	/** Set current to the first element of the list 
	 * TODO: make this return something, touching the global current is ugly
	 */
	private void goToStart() {
		// get the first vertex
		while ( current.previous != null )
			current = current.previous;
	}

	/**
	 * Get all edges whose projection onto the sweep direction overlap.
	 * 
	 * @return The numbers of the overlapping edges. The array will always have
	 * dimension [n][2], where [i][0] is the edge of polygon A and [i][1] of B.
	 */
	public int[][] getOverlappingEdges() {
		if ( current == null )
			return new int[0][2];
		
		goToStart();
		
		CurrentEdges edgesA = new CurrentEdges();
		CurrentEdges edgesB = new CurrentEdges();
		EdgePairs collidingEdges = new EdgePairs();
		
		float lastDist = -Float.MAX_VALUE;
		
		while ( current != null ) {
			if ( current.distance > lastDist ) {
				lastDist = current.distance;
				edgesA.removeScheduled();
				edgesB.removeScheduled();
			}
				
			if ( current.isA ) {
				if ( !edgesA.contains(current.vertex) ) {
					edgesA.addEdge(current.vertex);
					
					int[] edgeListB = edgesB.getEdges(); 
					for ( int i = 0; i < edgeListB.length; i++ )
						collidingEdges.add(current.vertex, edgeListB[i]);
					
				} else {
					edgesA.scheduleRemoval(current.vertex);
				}
			} else {
				if ( !edgesB.contains(current.vertex) ) {
					edgesB.addEdge(current.vertex);
					
					int[] edgeListA = edgesA.getEdges(); 
					for ( int i = 0; i < edgeListA.length; i++ )
						collidingEdges.add(edgeListA[i], current.vertex);
					
				} else {
					edgesB.scheduleRemoval(current.vertex);
				}
			}	
			
			current = current.next;
		}
		
		return collidingEdges.toList();
	}
	
	/** The list of edges that are touched by the sweepline at a given time. 
	 * 
	 * Note that this implementation proved faster than one with a HashSet,
	 * a specialized IntegerSet library and BitSet. This is mostly because
	 * this list will rarely contain more than 10 edges at a time.
	 * The IntegerSet and BitSet implementation both had poor performance
	 * in the getEdges function.
	 * 
	 * TODO: implement this with a tree to improve the performance of 'contains'?
	 * If that is done, one should take care that it handles edges that are mostly
	 * sorted (because that will definately be the case).
	 */
	private class CurrentEdges {
		/** The first element of the list of edges that have been inserted */
		private LinkedEdgeList currentEdges;
		/** The edges that have been scheduled for removal but have not yet been removed */
		private LinkedEdgeList scheduledForRemoval;
		
		/**
		 * Add an edge to the top of the list.
		 * We do not check wether it is already in the list, but maybe this should
		 * be done to be on the safe side.
		 * TODO: do that
		 * 
		 * @param e The edge to be added
		 */
		public void addEdge(int e) {
			currentEdges = new LinkedEdgeList(e,currentEdges);
		}
		
		/**
		 * Schedule an edge for removal, it will be removed as soon as 
		 * {@link CurrentEdges#removeScheduled()} is called.
		 * 
		 * @param e The edge to be scheduled for removal
		 */
		public void scheduleRemoval(int e) {
			if ( currentEdges == null )
				return; // this shouldn't happen, but to be sure..
			
			if ( currentEdges.edge == e ) {
				currentEdges = currentEdges.next;
			} else {
				LinkedEdgeList current = currentEdges.next;
				LinkedEdgeList last = currentEdges;
				
				while ( current != null ) {
					if ( current.edge == e ) {
						last.next = current.next;
						scheduledForRemoval = new LinkedEdgeList(e,scheduledForRemoval);
						return;
					}
					last = current;
					current = current.next;
				}
			}
		}
		
		/** Remove the edges that have been scheduled for removal by
		 * {@link CurrentEdges#scheduleRemoval(int)}. */
		public void removeScheduled() {
			scheduledForRemoval = null;
		}
		
		/**
		 * Check if this edge list contains a specific edge.
		 * 
		 * @param e The edge to look for
		 * @return True iff the edgelist contains the edge
		 */
		public boolean contains(int e) {
			LinkedEdgeList current = currentEdges;
			while ( current != null ) {
				if ( current.edge == e )
					return true;
				current = current.next;
			}
			
			current = scheduledForRemoval;
			while ( current != null ) {
				if ( current.edge == e )
					return true;
				current = current.next;
			}
			
			return false;
		}
		
		/**
		 * Get the total number of edges, this includes the edges that are
		 * scheduled for removal.
		 * 
		 * @return The total number of edges
		 */
		public int getNoEdges() {
			int count = 0;
			LinkedEdgeList current = currentEdges;
			while ( current != null ) {
				count++;
				current = current.next;
			}
			
			current = scheduledForRemoval;
			while ( current != null ) {
				count++;
				current = current.next;
			}
			
			return count;
		}
		
		/**
		 * Get the list of edges in this list.
		 * It should not contain any duplicates, but that depends on the insertion
		 * of elements.
		 * 
		 * @return the list of edges
		 */
		public int[] getEdges() {
			int[] returnEdges = new int[getNoEdges()];
			
			int i = 0;
			LinkedEdgeList current = currentEdges;
			while ( current != null ) {
				returnEdges[i] = current.edge;
				i++;
				current = current.next;
			}
			current = scheduledForRemoval;
			while ( current != null ) {
				returnEdges[i] = current.edge;
				i++;
				current = current.next;
			}
			
			return returnEdges;
		}
		
		/** A singly linked list for edges */
		class LinkedEdgeList {
			/** The edge number */
			public int edge;
			/** The next list element */
			public LinkedEdgeList next;
			
			/**
			 * Construct a new list element with its attributes set to the
			 * supplied values.
			 * 
			 * @param edge The edge number
			 * @param next The next list element
			 */
			public LinkedEdgeList(int edge, LinkedEdgeList next) {
				this.edge = edge;
				this.next = next;
			}
		}
	}
	
	/** The list of collision candidates in a linked list */
	private class EdgePairs {
		/** The first element of the list of collision candidates */ 
		private EdgePair first;
		/** The total number of collision candidates */
		private int size = 0;
		
		/** 
		 * Add a pair of edges to this list
		 *
		 * @param idA An edge of polygon A
		 * @param idB An edge of polygon B 
		 */
		public void add(int idA, int idB) {
			first = new EdgePair(idA, idB, first);
			size++;
		}
		
		/**
		 * Convert this linked list into a two dimensional array
		 * 
		 * @return The numbers of the overlapping edges. The array will always have
		 * dimension [n][2], where [i][0] is the edge of polygon A and [i][1] of B.
		 */
		public int[][] toList() {
			int[][] list = new int[size][2];
			
			EdgePair current = first;
			for ( int i = 0; i < size; i++ ) {
				list[i][0] = current.a;
				list[i][1] = current.b;
				
				current = current.next;
			}
			
			return list;
		}
		
		/** The singly linked list representing one pair of edges */
		class EdgePair {
			/** An edge of polygon A */
			public int a;
			/** An edge of polygon B */
			public int b;
			/** The next element in the list */
			public EdgePair next;
			
			/** Construct a new list element with all the attributes set to the
			 * provided values.
			 * 
			 * @param a An edge of polygon A
			 * @param b An edge of polygon B
			 * @param next The next element in the list
			 */
			public EdgePair(int a, int b, EdgePair next) {
				this.a = a;
				this.b = b;
				this.next = next;
			}
		}
	}
	
	/**
	 * Insert a list of edges
	 * 
	 * @param isA True iff the inserted vertices are of the first object
	 * @param verts The list of vertices to be inserted in counter clockwise order
	 */
	public void addVerticesToSweep(boolean isA, Vector2f[] verts) {
		for ( int i = 0, j = verts.length-1; i < verts.length; j = i, i++ ) {
			float dist = sweepDir.dot(verts[i]);
			
			insert(i, isA, dist);
			insert(j, isA, dist);
		}
	}
	
	/**
	 * Get the direction of this edgesweep
	 * 
	 * @return the direction of this edgesweep
	 */
	public ROVector2f getSweepDir() {
		return sweepDir;
	}
}
