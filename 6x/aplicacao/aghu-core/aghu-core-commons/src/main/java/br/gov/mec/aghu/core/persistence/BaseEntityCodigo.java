package br.gov.mec.aghu.core.persistence;

import javax.persistence.Id;

public abstract class BaseEntityCodigo<ID> implements BaseEntity {
	
	private static final long serialVersionUID = 6367764617617530593L;

	@Id
    public abstract ID getCodigo();

    public abstract void setCodigo(ID id);

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getCodigo() != null ? getCodigo().hashCode() : 0);
        return hash;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        
        if (obj == null){
             return false;
        }
        else if (!(obj instanceof BaseEntityCodigo)){
             return false;
        }
        else if (((BaseEntityCodigo<ID>) obj).getCodigo().equals(this.getCodigo())){
             return true;
        }
        else{
             return false;
        }
    }

    public String toString() {
        return this.getClass().getName()+"[ codigo=" + getCodigo() + " ]";
    }
    
}
