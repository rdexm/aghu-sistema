package br.gov.mec.aghu.casca.autenticacao;

import java.io.Serializable;
import java.security.Principal;

/**
 * Classe para simular usuario logado para Jobs do Quartz.
 * 
 */
public class AghuQuartzPrincipal implements Principal, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1019631512731404359L;
	
	
	private String name;
	
	public AghuQuartzPrincipal(String namePrincipal) {
		if (namePrincipal == null) {
			throw new IllegalArgumentException("name cannot be null");
		}

		this.name = namePrincipal;
	}


	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Return a string representation of this <code>SamplePrincipal</code>.
	 * 
	 * <p>
	 * 
	 * @return a string representation of this <code>SamplePrincipal</code>.
	 */
	public String toString() {
		return "AghuPrincipal:  " + name;
	}

	/**
	 * Compares the specified Object with this <code>SamplePrincipal</code> for
	 * equality. Returns true if the given object is also a
	 * <code>SamplePrincipal</code> and the two SamplePrincipals have the same
	 * username.
	 * 
	 * <p>
	 * 
	 * @param o
	 *            Object to be compared for equality with this
	 *            <code>SamplePrincipal</code>.
	 * 
	 * @return true if the specified Object is equal equal to this
	 *         <code>SamplePrincipal</code>.
	 */
	public boolean equals(Object o) {
		if (o == null){
			return false;
		}

		if (this == o){
			return true;
		}

		if (!(o instanceof AghuQuartzPrincipal)){
			return false;
		}
		AghuQuartzPrincipal that = (AghuQuartzPrincipal) o;

		if (this.getName().equals(that.getName())){
			return true;
		}
		return false;
	}

	/**
	 * Return a hash code for this <code>SamplePrincipal</code>.
	 * 
	 * <p>
	 * 
	 * @return a hash code for this <code>SamplePrincipal</code>.
	 */
	public int hashCode() {
		return name.hashCode();
	}

}
