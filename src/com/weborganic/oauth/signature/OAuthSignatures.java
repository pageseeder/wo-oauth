package com.weborganic.oauth.signature;

import java.util.HashMap;
import java.util.Map;

import com.weborganic.oauth.OAuthException;
import com.weborganic.oauth.OAuthProblem;
import com.weborganic.oauth.util.Preconditions;

/**
 * A utility class to make it easier to deal with OAuth signatures.
 * 
 * @author Christophe Lauret
 * @version 21 July 2011
 */
public final class OAuthSignatures {

  /**
   * Maps OAuth signature methods names to signers class.
   */
  private final static Map<String, Class<? extends OAuthSigner>> SIGNER_FOR_NAME = new HashMap<String, Class<? extends OAuthSigner>>();

  // Register the built-in signers first
  static {
    register("HMAC-SHA1", HMACSha1Signer.class);
    register("PLAINTEXT", PlainTextSigner.class);
  }

  /** 
   * Utility class.
   */
  private OAuthSignatures() {
  }

  /**
   * Registers a new signer
   */
  public static synchronized void register(String name, Class<? extends OAuthSigner> signerClass) {
    SIGNER_FOR_NAME.put(name, signerClass);
  }

  /**
   * A factory method for signers.
   * 
   * @param name The name of the OAuth signature method requested.
   * @return The corresponding signer.
   * 
   * @throws OAuthException If a signer could not be created.
   */
  public static OAuthSigner newSigner(String name) throws OAuthException {
    Preconditions.checkNotNull(name, "The signature method is null");
    try {
      Class<? extends OAuthSigner> signerClass = SIGNER_FOR_NAME.get(name);
      if (signerClass != null) {
        OAuthSigner signer = signerClass.newInstance();
        return signer;
      }
      // TODO include supported methods
      throw new OAuthException(OAuthProblem.signature_method_rejected);
    } catch (InstantiationException ex) {
      throw new OAuthException(OAuthProblem.signature_method_rejected, ex);
    } catch (IllegalAccessException ex) {
      throw new OAuthException(OAuthProblem.signature_method_rejected, ex);
    }
  }

}
