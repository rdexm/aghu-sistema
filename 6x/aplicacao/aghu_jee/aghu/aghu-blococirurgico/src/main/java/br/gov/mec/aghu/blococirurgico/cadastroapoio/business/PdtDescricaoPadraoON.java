package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescPadraoDAO;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtDescPadraoId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PdtDescricaoPadraoON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(PdtDescricaoPadraoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescPadraoDAO pdtDescPadraoDAO;


	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private PdtDescricaoPadraoRN pdtDescricaoPadraoRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long serialVersionUID = 1403535177734724903L;
	
	
	protected enum PdtDescricaoPadraoONCode implements BusinessExceptionCode {
		REGISTRO_NULO_BUSCA;
	}
	
	public String persistirPdtDescPadrao(PdtDescPadrao descricaoPadrao)  throws ApplicationBusinessException{
		
		String msgRetorno;
		
		if (descricaoPadrao.getId().getSeqp() == null){
				//RN1-BRI
				descricaoPadrao.setCriadoEm(new Date());
				//RN2-BRI
				descricaoPadrao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
				getPdtDescricaoPadraoRN().atualizarServidor();
				//carrega ID
				Short espSeq = descricaoPadrao.getAghEspecialidades().getSeq();
				Short seqp = getPdtDescPadraoDAO().obterMaxSeqpByEspSeq(espSeq);
				seqp++;
				descricaoPadrao.setId(new PdtDescPadraoId(espSeq,seqp));
				
				getPdtDescPadraoDAO().persistir(descricaoPadrao);
				msgRetorno = "MENSAGEM_SUCESSO_GRAVAR_DESCRICAO_PADRAO_PDT";
		}else{
			verificarExisteRegistro(descricaoPadrao);
			//RN1-BRU
			getPdtDescricaoPadraoRN().atualizarServidor();
			getPdtDescPadraoDAO().atualizar(descricaoPadrao);
			//RN1-ARU
			getPdtDescricaoPadraoRN().posUpdatePdtDescPadrao(descricaoPadrao);
			msgRetorno = "MENSAGEM_SUCESSO_ALTERACAO_DESCRICAO_PADRAO_PDT";
		}
		return msgRetorno;
	}
	
	public String removerPdtDescPadrao(PdtDescPadrao descricaoPadrao){
		PdtDescPadrao pdtDescPadraoDel = getPdtDescPadraoDAO().obterPorChavePrimaria(descricaoPadrao.getId());
		getPdtDescPadraoDAO().remover(pdtDescPadraoDel);
		getPdtDescricaoPadraoRN().posDeletePdtDescPadrao(pdtDescPadraoDel);
		return "MENSAGEM_SUCESSO_EXCLUSAO_DESCRICAO_PADRAO_PDT";
	}
	
	public void verificarExisteRegistro(PdtDescPadrao descricaoPadrao) throws ApplicationBusinessException{
		PdtDescPadrao entidade = getPdtDescPadraoDAO().obterOriginal(descricaoPadrao.getId());
		if (entidade == null) {
			throw new ApplicationBusinessException(PdtDescricaoPadraoONCode.REGISTRO_NULO_BUSCA);
		}
	}

	protected PdtDescPadraoDAO getPdtDescPadraoDAO() {
		return pdtDescPadraoDAO;
	}
	
	private PdtDescricaoPadraoRN getPdtDescricaoPadraoRN() {
		return pdtDescricaoPadraoRN;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}


}
