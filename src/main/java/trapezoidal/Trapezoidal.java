package trapezoidal;

import mpi.MPI;

public class Trapezoidal {

	public static void main( final String... args ) {
		MPI.Init( args );
		final int rank = MPI.COMM_WORLD.Rank();
		final int size = MPI.COMM_WORLD.Size();

		// Problem is from a to b
		final int n = 1024;
		final double a = 0;
		final double b = 3;
		final double h = ( b - a ) / n;

		// We will do from ar to br
		int nr = n / size;
		final double ar = a + rank * nr * h;
		double br = ar + nr * h;

		// Last rank includes any remaining
		if ( size - 1 == rank ) {
			nr += n - size * nr;
			br = b;
		}

		// Calculate our part
		double area = trapezoidal( ar, nr, h );
		System.out.println( ar + " -> " + br + ": " + area );

		final double[] data = { area };
		final double[] total = new double[ 1 ];
		MPI.COMM_WORLD.Reduce( data, 0, total, 0, 1, MPI.DOUBLE, MPI.SUM, 0 );

		if ( rank == 0 ) {
			System.out.println( a + " -> " + b + ": " + total[ 0 ] );
		}

		MPI.Finalize();
	}

	private static double trapezoidal( final double a, final int n, final double h ) {

		double area = Math.sin( a ) + Math.sin( a + n * h );
		area = area / 2;

		for ( int i = 1; i < n; i++ ) {
			area += Math.sin( a + i * h );
		}

		return area * h;
	}
}
