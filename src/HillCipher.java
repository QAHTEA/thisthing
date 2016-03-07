import java.math.BigInteger;

/**
 * Created by Sandwich
 */

public class HillCipher {
    private static final int MIN_MATRIX_SIZE = 3; // min 3 x 3 matrix
    private static final int OFFSET = 65; // to start alphabet index at 0
    private static final int MOD26 = 26; // often used
    private static final int BLOCK = 3; // size of block

    private static int[][] KEY = new int[MIN_MATRIX_SIZE][MIN_MATRIX_SIZE]; // matrix to hold key
    private static final int[] KEY_SOURCE =  /**{	15,10,29, // the key
										    	8,17,23, 
										    	8,13,5};**/ 
    {17,17,5,21,18,21,2,2,19};

    public HillCipher() {
        int k = 0;
        for (int i = 0; i < BLOCK; i++)
            for (int j = 0; j < BLOCK; j++)
                KEY[i][j] = KEY_SOURCE[k++]; // put the key into the matrix
    }

    public String Encrypt(String _plainText){
        _plainText = _plainText.toUpperCase();
    	_plainText = _plainText.replaceAll("\\s", ""); // replace spaces with X
    	int padding =  BLOCK - (_plainText.length() % BLOCK);
        while (padding > 0 && padding < 3) {
            _plainText += "X";
            padding--;
        }

        StringBuilder sb = new StringBuilder();
        int complete = 0;
        while (complete != _plainText.length()) {
            String block = _plainText.substring(complete, complete+3);
            complete += 3;

            for (int row = 0; row < BLOCK; row++) {
                int temp = 0;
                for (int col = 0; col < BLOCK; col++) {
                    int number = (int)(block.charAt(col) - OFFSET); // get index in alphabet
                    temp += (KEY[row][col] * number);
                }
                temp = (temp % MOD26) + OFFSET; // mod 26, add offset to get alphabet letter
                sb.append((char)temp);
            }
        }
        return sb.toString();
    }
    
    public String Decrypt(String _cipherText) {
    	
    	StringBuilder sb = new StringBuilder();
    	int[][] Determinant = findDeterminant();
    	int complete = 0;
        while (complete != _cipherText.length()) {
            String block = _cipherText.substring(complete, complete+3);
            complete += 3;

            for (int row = 0; row < BLOCK; row++) {
                int temp = 0;
                for (int col = 0; col < BLOCK; col++) {
                    int number = (int)(block.charAt(col) - OFFSET); // get index in alphabet
                    temp += (Determinant[row][col] * number);
                }
                temp = modulus(temp); // mod 26, add offset to get alphabet letter
                sb.append((char)(temp + OFFSET));
            }
        }
    	
    	return sb.toString();
    }
    
    private int[][] findDeterminant() {
    	int[][] DeterminantKey = new int[MIN_MATRIX_SIZE][MIN_MATRIX_SIZE];  	
    	int row = 0;
    	int value = 0;
    	
    	for (row = 0; row < MIN_MATRIX_SIZE; row++)
			for (int col = 0; col < MIN_MATRIX_SIZE; col++){ // for each column in row 0
				int[][] subMatrix = new int[2][2]; // create a sub matrix
	    	    int smi = 0; // submatrix i
	    	    int smj = 0; // submatrix j
		    	for (int i = 0; i < MIN_MATRIX_SIZE; i++) {
	    			smj = 0; // reset submatrix j
	    			boolean value_added = false; // if value wasn't skipped (for being in same row or column)
		    		for (int j = 0; j < MIN_MATRIX_SIZE; j++) {
		    			if (i != row && j != col) { // exclude for being in chosen row or column or continue
			    			subMatrix[smi][smj] = KEY[i][j]; // submatrix value becomes key value
			    			System.out.print(subMatrix[smi][smj] + " ");
			    			smj++; // increment submatrix j
			    			value_added = true; 
		    			}
		    		}
		    		System.out.println();
		    		if (value_added) smi++; // only increment submatrix i if a value was added
		    	}
		    	// multiply this value of the key, with the submatrix to get the determinant of this value
		    	int val = (subMatrix[0][0] * subMatrix[1][1]) - (subMatrix[0][1] * subMatrix[1][0]);//KEY[row][col] * ((subMatrix[0][0] * subMatrix[1][1]) - (subMatrix[0][1] * subMatrix[1][0]));
		    	DeterminantKey[row][col] = val;//(subMatrix[0][0] * subMatrix[1][1]) - (subMatrix[0][1] * subMatrix[1][0]);		    	
	    		System.out.println(val);
	    		value += val;
	    		System.out.println(value);
		    	
	    	}
		//if (value < 0) value *= -1;
    	int reciprocalModulo = inverseModulus(value);
    	System.out.println("reciprocal Modulo = "+ reciprocalModulo);
    	
    	DeterminantKey = transposeMatrix(DeterminantKey);
    	System.out.println("Transposed Matrix modulus 26: ");
    	for (int dkrow = 0; dkrow < MIN_MATRIX_SIZE; dkrow++) {
    		for (int dkcol = 0; dkcol < MIN_MATRIX_SIZE; dkcol++) { 
    			DeterminantKey[dkrow][dkcol] = modulus(DeterminantKey[dkrow][dkcol]);// % MOD26;
    			System.out.print(DeterminantKey[dkrow][dkcol] + " ");
    		}
    		System.out.println();
    	}
    	 
    	System.out.println("Multiplied by recip modulus: ");
    	for (int dkrow = 0; dkrow < MIN_MATRIX_SIZE; dkrow++) {
    		for (int dkcol = 0; dkcol < MIN_MATRIX_SIZE; dkcol++) { 
    			DeterminantKey[dkrow][dkcol] = reciprocalModulo * DeterminantKey[dkrow][dkcol];
    			System.out.print(DeterminantKey[dkrow][dkcol] + " ");
    		}
    		System.out.println();
    	}
    	
    	System.out.println("Modulus 26 for final matrix:");
    	for (int dkrow = 0; dkrow < MIN_MATRIX_SIZE; dkrow++) {
    		for (int dkcol = 0; dkcol < MIN_MATRIX_SIZE; dkcol++) { 
    			DeterminantKey[dkrow][dkcol] = DeterminantKey[dkrow][dkcol] % MOD26;
    			System.out.print(DeterminantKey[dkrow][dkcol] + " ");
    		}
    		System.out.println();
    	}
    	/**
    	
    	
    	
    	*/
    	return DeterminantKey;
    }
    
    private int inverseModulus(int _value){
//    	int remainder = _value % MOD26;
//    	System.out.println("remainder: " + remainder);
//    	int tmep = _value + remainder / MOD26;// + remainder;    	
//    	return tmep;
//    	
//    	BigInteger bigint = BigInteger.valueOf(_value);
//    	bigint = bigint.modInverse(BigInteger.valueOf(MOD26));
//    	//int inverse = _value * MOD26 + (_value % MOD26); 
//    	return bigint.intValue();///*MOD26 - */(_value % MOD26);
    	
    	return (MOD26 - (_value % MOD26));
    }
    
    private int modulus(int _val) {
    	return (_val % MOD26 + MOD26) % MOD26;
    }
    
    private int[][] transposeMatrix(int[][] _matrix){
    	int newrow = MIN_MATRIX_SIZE - 1;
    	int newcol = MIN_MATRIX_SIZE - 1;
    	int [][] newmatrix = new int[MIN_MATRIX_SIZE][MIN_MATRIX_SIZE];
    	int variant = 1;
    	System.out.println("Transposed Matrix: ");
    	for (int row = 0; row < MIN_MATRIX_SIZE; row++) {
    		for (int col = 0; col < MIN_MATRIX_SIZE; col++){
    			newmatrix[newrow][newcol] = _matrix[row][col] * variant;
    			System.out.print(newmatrix[newrow][newcol] + " ");
    			variant *= -1;
    			newcol--;
    		}
			newrow--;
			newcol = MIN_MATRIX_SIZE -1;
			System.out.println();
    	}    	
    	return newmatrix;
    }
    

    public static void main(String[] args) {
        HillCipher hill_cipher = new HillCipher();
        String msg = hill_cipher.Encrypt("pay more money");
        System.out.println("message: pay more money\nencrypted message: " + msg);
        System.out.println("decrypted message: " + hill_cipher.Decrypt(msg));
    }
}
