package edu.cedarville.cs.crypto;

import java.lang.Integer;

public class TinyE {
	
	public static enum Mode { ECB, CBC, CTR };

	// Delta for TEA encryption rounds
	private static final int DELTA = 0x9e3779b9;
	
	public Integer[] encrypt(Integer[] plaintext, Integer[] key, Mode mode, Integer[] iv) {
		Integer[] cyphertext = new Integer[plaintext.length];

		// Encrypt each block of the plaintext
		// Each block in The TEA cipher is 64 bits so we need to jump 2 spaces each time
		for(int i = 0; i < plaintext.length; i += 2){
			Integer[] encryptedBlock = null;
			if(mode == Mode.ECB || mode == Mode.CBC) {
				// if we are in CBC mode we need to XOR the plaintext block with the IV before we encrypt it.
				if (mode == Mode.CBC) {
					plaintext[i] ^= iv[0];
					plaintext[i + 1] ^= iv[1];
				}

				encryptedBlock  = encryptBlock(plaintext[i], plaintext[i + 1], key);
				// Set iv to the block we just encrypted so we can use it on the next block.
				iv = encryptedBlock;


			} else if(mode == Mode.CTR){
				encryptedBlock = ctrAlgorithm(plaintext[i], plaintext[i + 1], i / 2, iv, key);
			}

			// Write the new block of cyphertext to the resulting array.
			cyphertext[i] = encryptedBlock[0];
			cyphertext[i + 1] = encryptedBlock[1];
		}

		return cyphertext;
	}

	private static Integer[] ctrAlgorithm(int lh, int rh, int blockNumber, Integer[] iv, Integer[] key){
		Integer[] result = new Integer[2];
		// CTR mode is basically a stream cypher so we need to get the key for this block.
		Integer[] ctrKey = getCTRKey(blockNumber, iv, key);

		result[0] = lh ^ ctrKey[0];
		result[1] = rh ^ ctrKey[1];

		return result;
	}

	private static Integer[] getCTRKey(int blockNumber, Integer[] iv, Integer[] key){
		// We need to convert IV to a long so that we can properly add in the block number
		long ivAsLong = (iv[0] << 32) + iv[1];
		ivAsLong += blockNumber;

		// Now we need to convert IV back to integers so that we can use it in encryptBlock
		return encryptBlock((int)(ivAsLong >>> 32), (int)(ivAsLong & 0x00000000FFFFFFFF), key);
	}

	private static Integer[] encryptBlock(int lh, int rh, Integer[] key){
		Integer[] result = new Integer[2];
		int sum = 0;

		// Do the 32 encryption rounds.
		for(int i = 0; i < 32; i++){
			sum += DELTA;
			lh = lh + (((rh << 4) + key[0]) ^ (rh + sum) ^ ((rh >> 5) + key[1]));
			rh = rh + (((lh << 4) + key[2]) ^ (lh + sum) ^ ((lh >> 5) + key[3]));
		}

		// Write the left and right halvs to the result array.
		result[0] = lh;
		result[1] = rh;

		return result;
	}

	public Integer[] decrypt(Integer[] ciphertext, Integer[] key, Mode mode, Integer[] iv) {
		Integer[] plaintext = new Integer[ciphertext.length];

		//if iv hasn't been passed we need to allocate an array
		if(mode == Mode.ECB && iv == null){
			iv = new Integer[2];
		}

		//Decrypt each block of plaintext
		//Each block in the TEA cipher is 64 bits so we need to jump 2 spaces each time
		for(int i = 0; i < ciphertext.length; i += 2){
			Integer[] decryptedBlock = null;
			if(mode == Mode.ECB || mode == Mode.CBC){
				decryptedBlock = decryptBlock(ciphertext[i], ciphertext[i + 1], key);

				// If we are in CBC mode we need to XOR the bits by IV to finish decrypting them
				if(mode == Mode.CBC){
					decryptedBlock[0] ^= iv[0];
					decryptedBlock[1] ^= iv[1];
				}

				//Set iv to the previous encrypted block to use in decrypting the next block.
				iv[0] = ciphertext[i];
				iv[1] = ciphertext[i + 1];
			} else if(mode == Mode.CTR){
				decryptedBlock = ctrAlgorithm(ciphertext[i], ciphertext[i + 1], i / 2, key, iv);
			}

			// Write the new block of plaintext to the resulting array.
			plaintext[i] = decryptedBlock[0];
			plaintext[i + 1] = decryptedBlock[1];
		}

		return plaintext;
	}

	private static Integer[] decryptBlock(int lh, int rh, Integer[] key){
		Integer[] result = new Integer[2];
		int sum = DELTA << 5;

		for(int i = 0; i < 32; i++){
			rh = rh - (((lh << 4) + key[2]) ^ (lh + sum) ^ ((lh >> 5) + key[3]));
			lh = lh - (((rh << 4) + key[0]) ^ (rh + sum) ^ ((rh >> 5) + key[1]));
			sum -= DELTA;
		}

		// Write the left and right halves to the result array.
		result[0] = lh;
		result[1] = rh;

		return result;
	}
	
}
