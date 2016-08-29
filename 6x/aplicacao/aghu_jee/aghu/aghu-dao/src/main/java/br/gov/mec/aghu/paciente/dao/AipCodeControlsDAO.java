package br.gov.mec.aghu.paciente.dao;

import org.hibernate.LockOptions;

import br.gov.mec.aghu.model.AipCodeControls;

public class AipCodeControlsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipCodeControls> {

	private static final long serialVersionUID = 6499658488709779482L;

	public AipCodeControls obterCodeControl(String id, LockOptions lockMode) {
		return (AipCodeControls) getAndLock( id, lockMode);
	}
	
	public AipCodeControls obterCodeControlLockForce(String id) {
		return (AipCodeControls) getAndLockForce( id );
	}

}
