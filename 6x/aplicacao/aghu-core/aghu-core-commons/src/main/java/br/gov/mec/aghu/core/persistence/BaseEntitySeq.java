package br.gov.mec.aghu.core.persistence;

import javax.persistence.Id;

public abstract class BaseEntitySeq<ID> implements BaseEntity {

	private static final long serialVersionUID = 8164404690402366699L;

	@Id
    public abstract ID getSeq();

    public abstract void setSeq(ID id);

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getSeq() != null ? getSeq().hashCode() : 0);
        return hash;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        
        if (obj == null){
             return false;
        }
        else if (!(obj instanceof BaseEntitySeq)){
             return false;
        }
        else if (((BaseEntitySeq<ID>) obj).getSeq().equals(this.getSeq())){
             return true;
        }
        else{
             return false;
        }
    }

    public String toString() {
        return this.getClass().getName()+"[ seq=" + getSeq() + " ]";
    }
    
}