package br.gov.mec.aghu.core.persistence;

public abstract class BaseEntityId<ID> implements BaseEntity  {
    
	private static final long serialVersionUID = -2392169359571950817L;
	
    public abstract ID getId();

    public abstract void setId(ID id);    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        if (obj == null){
             return false;
        }
        else if (!(obj instanceof BaseEntity)){
             return false;
        }
        else if (((BaseEntityId<ID>) obj).getId().equals(this.getId())){
             return true;
        }
        else{
             return false;
        }
    }

    public String toString() {
        return this.getClass().getName()+"[ id=" + this.getId() + " ]";
    }
}