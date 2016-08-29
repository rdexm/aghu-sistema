package br.gov.mec.aghu.core.persistence;

import javax.persistence.Id;

public abstract class BaseEntityNumero<ID> implements BaseEntity {
    
	private static final long serialVersionUID = 8664496633974799614L;

	@Id
    public abstract ID getNumero();

    public abstract void setNumero(ID id);

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getNumero() != null ? getNumero().hashCode() : 0);
        return hash;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        
        if (obj == null){
             return false;
        }
        else if (!(obj instanceof BaseEntityNumero)){
             return false;
        }
        else if (((BaseEntityNumero<ID>) obj).getNumero().equals(this.getNumero())){
             return true;
        }
        else{
             return false;
        }
    }

    public String toString() {
        return this.getClass().getName()+"[ codigo=" + getNumero() + " ]";
    }
    
}
