package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelServidorUnidAssinaEletDAO;
import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.AelServidorUnidAssinaEletId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelServidorUnidAssinaEletON extends BaseBusiness {

	@EJB
	private AelServidorUnidAssinaEletRN aelServidorUnidAssinaEletRN;
	
	private static final Log LOG = LogFactory.getLog(AelServidorUnidAssinaEletON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private AelServidorUnidAssinaEletDAO aelServidorUnidAssinaEletDAO;

	private static final long serialVersionUID = -2876524483275806974L;

	public enum AelServidorUnidAssinaEletONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVIDOR_CADASTRADO_NA_UNIDADE
	}

	public void inserirAelServidorUnidAssinaElet(AghUnidadesFuncionais unidadeFuncional, RapServidores servidor, Boolean ativo) throws ApplicationBusinessException {
		AelServidorUnidAssinaEletId servidorUnidAssinaEletId = new AelServidorUnidAssinaEletId(unidadeFuncional.getSeq(), servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		AelServidorUnidAssinaElet servidorUnidAssinaEletron = getCadastrosApoioExamesFacade().obterAelServidorUnidAssinaEletPorId(servidorUnidAssinaEletId);
		
		if (servidorUnidAssinaEletron == null){			
			AelServidorUnidAssinaElet servidorUnidAssinaElet = new AelServidorUnidAssinaElet();			
			
			getAelServidorUnidAssinaEletRN().aeltSuaBri(servidorUnidAssinaElet);
			
			servidorUnidAssinaElet.setId(servidorUnidAssinaEletId);
			servidorUnidAssinaElet.setServidor(servidor);
			DominioSituacao situacao;
			if(ativo){
				situacao = (DominioSituacao.A);
			}else{
				situacao = (DominioSituacao.I);
			}
			servidorUnidAssinaElet.setSituacao(situacao);
			servidorUnidAssinaElet.setAghUnidadesFuncionais(unidadeFuncional);
			
			this.getAelServidorUnidAssinaEletDAO().persistir(servidorUnidAssinaElet);		
		}else{
			throw new ApplicationBusinessException(AelServidorUnidAssinaEletONExceptionCode.MENSAGEM_SERVIDOR_CADASTRADO_NA_UNIDADE, servidor.getPessoaFisica().getNome(), unidadeFuncional.getDescricao());
		}		
	}
	
	public void atualizarAelServidorUnidAssinaElet(AelServidorUnidAssinaElet servidorUnidAssinaElet) throws ApplicationBusinessException {
		getAelServidorUnidAssinaEletRN().aeltSuaBru(servidorUnidAssinaElet);
		servidorUnidAssinaElet.setSituacao(servidorUnidAssinaElet.getSituacao().equals(DominioSituacao.I)?DominioSituacao.A:DominioSituacao.I);		
		this.getAelServidorUnidAssinaEletDAO().merge(servidorUnidAssinaElet);		
	}	

	protected AelServidorUnidAssinaEletRN getAelServidorUnidAssinaEletRN(){
		return aelServidorUnidAssinaEletRN;
	}

	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}
	
	protected AelServidorUnidAssinaEletDAO getAelServidorUnidAssinaEletDAO() {
		return aelServidorUnidAssinaEletDAO;
	}

}