package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceBoletimOcorrenciasDAO;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SceBoletimOcorrenciasRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceBoletimOcorrenciasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceBoletimOcorrenciasDAO sceBoletimOcorrenciasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7731239316628199313L;

	private void preInserir(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		//
	}
	
	private void posInserir(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		//
	}
	
	public void inserir(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		this.preInserir(boletimOcorrencia);
		this.getSceBoletimOcorrenciasDAO().persistir(boletimOcorrencia);
		this.posInserir(boletimOcorrencia);
	}
	
	private void preAtualizar(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		//
	}

	private void posAtualizar(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		//
	}

	public void atualizar(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		this.preAtualizar(boletimOcorrencia);
		this.getSceBoletimOcorrenciasDAO().atualizar(boletimOcorrencia);
		this.posAtualizar(boletimOcorrencia);
	}

	@SuppressWarnings("ucd")
	public void remover(SceBoletimOcorrencias boletimOcorrencia) throws ApplicationBusinessException {
		this.getSceBoletimOcorrenciasDAO().remover(boletimOcorrencia);
	}
	
	private SceBoletimOcorrenciasDAO getSceBoletimOcorrenciasDAO() {
		return sceBoletimOcorrenciasDAO;
	}	
}
