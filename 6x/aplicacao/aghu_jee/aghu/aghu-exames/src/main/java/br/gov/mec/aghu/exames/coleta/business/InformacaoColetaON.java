package br.gov.mec.aghu.exames.coleta.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelInformacaoColetaDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaHistDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaDAO;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaHist;
import br.gov.mec.aghu.model.AelInformacaoColetaHistId;
import br.gov.mec.aghu.model.AelInformacaoColetaId;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColetaId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de negócio para as 
 * entidades AelInformacaoColeta e AelInformacaoMdtoColeta.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class InformacaoColetaON extends BaseBusiness {

	
	@EJB
	private InformacaoMdtoColetaRN informacaoMdtoColetaRN;
	
	@EJB
	private InformacaoColetaRN informacaoColetaRN;
	
	private static final Log LOG = LogFactory.getLog(InformacaoColetaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AelInformacaoColetaDAO aelInformacaoColetaDAO;
	
	@Inject
	private AelInformacaoColetaHistDAO aelInformacaoColetaHistDAO;
	
	
	@Inject
	private AelInformacaoMdtoColetaDAO aelInformacaoMdtoColetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6873795387127318498L;

	private static final Short INFORMACAO_COLETA_SEQP_INICIAL = 1;

	/**
	 * Persiste objetos AelInformacaoColeta e AelInformacaoMdtoColeta.
	 * 
	 * Utiliza flush() devido à necessidade de replicar objetos AelInformacaoColeta
	 * e AelInformacaoMdtoColeta (possui referencia para AelInformacaoColeta)
	 * para cada instancia de uma lista de AelSolicitacaoExames.
	 * 
	 * @param informacaoColetaNew
	 * @param listaInformacaoMdtoColetaNew
	 * @param listaSolicitacaoExames
	 *  
	 */
	public AelInformacaoColeta persistirInformacaoColeta(AelInformacaoColeta informacaoColetaNew,
			List<AelInformacaoMdtoColeta> listaInformacaoMdtoColetaNew, 
			List<AelSolicitacaoExames> listaSolicitacaoExames) throws BaseException {

		AelInformacaoColetaDAO informacaoColetaDAO = getAelInformacaoColetaDAO();

		for (AelSolicitacaoExames solicitacaoExame : listaSolicitacaoExames) {
//			Integer iclSoeSeq = solicitacaoExame.getSeq();
			AelInformacaoColeta copiaInformacaoColetaNew = null;

				copiaInformacaoColetaNew = cloneAelInformacaoColeta(informacaoColetaNew, solicitacaoExame);
//				copiaInformacaoColetaNew = (AelInformacaoColeta) BeanUtils.cloneBean(informacaoColetaNew);
			AelInformacaoColeta informacaoColeta = informacaoColetaDAO.obterPorChavePrimaria(copiaInformacaoColetaNew.getId());


			if (informacaoColeta == null) {
				copiaInformacaoColetaNew.setSolicitacaoExame(solicitacaoExame);
				getInformacaoColetaRN().inserirInformacaoColeta(copiaInformacaoColetaNew);
			} else {
				informacaoColeta.setCumpriuJejum(copiaInformacaoColetaNew.getCumpriuJejum());
				informacaoColeta.setJejumRealizado(copiaInformacaoColetaNew.getJejumRealizado());
				informacaoColeta.setDocumento(copiaInformacaoColetaNew.getDocumento());
				informacaoColeta.setTipoAcesso(copiaInformacaoColetaNew.getTipoAcesso());
				informacaoColeta.setDtUltMenstruacao(copiaInformacaoColetaNew.getDtUltMenstruacao());
				informacaoColeta.setInfMenstruacao(copiaInformacaoColetaNew.getInfMenstruacao());
				informacaoColeta.setInfMedicacao(copiaInformacaoColetaNew.getInfMedicacao());
				informacaoColeta.setLocalColeta(copiaInformacaoColetaNew.getLocalColeta());
				informacaoColeta.setInformacoesAdicionais(copiaInformacaoColetaNew.getInformacoesAdicionais());
				getInformacaoColetaRN().atualizarInformacaoColeta(informacaoColeta);
			}

			AelInformacaoMdtoColetaDAO  dao = getAelInformacaoMdtoColetaDAO(); 
			for (AelInformacaoMdtoColeta infoMedicamento : listaInformacaoMdtoColetaNew){
				if (informacaoColeta == null) {
						AelInformacaoMdtoColeta infoMedicamentoNew = new AelInformacaoMdtoColeta();
						AelInformacaoMdtoColetaId aelInformacaoMdtoColetaId = new AelInformacaoMdtoColetaId();
						aelInformacaoMdtoColetaId.setIclSoeSeq(copiaInformacaoColetaNew.getId().getSoeSeq());
						aelInformacaoMdtoColetaId.setIclSeqp(copiaInformacaoColetaNew.getId().getSeqp());
						aelInformacaoMdtoColetaId.setSeqp(infoMedicamento.getId().getSeqp());
						infoMedicamentoNew.setId(aelInformacaoMdtoColetaId);
						infoMedicamentoNew.setInformacaoColeta(copiaInformacaoColetaNew);

						infoMedicamentoNew.setDthrIngeriu(infoMedicamento.getDthrIngeriu());
						infoMedicamentoNew.setMedicamento(infoMedicamento.getMedicamento());
						infoMedicamentoNew.setDthrColetou(infoMedicamento.getDthrColetou());

						getInformacaoMdtoColetaRN().inserirInformacaoMdtoColeta(infoMedicamentoNew);
						copiaInformacaoColetaNew.getInformacaoMdtoColetaes().add(infoMedicamentoNew);
					}else {
						infoMedicamento.setInformacaoColeta(informacaoColeta);
						if (dao.obterPorChavePrimaria(infoMedicamento.getId()) != null) {
							getInformacaoMdtoColetaRN().atualizarInformacaoMdtoColeta(infoMedicamento);
						} else {
							getInformacaoMdtoColetaRN().inserirInformacaoMdtoColeta(infoMedicamento);
							informacaoColetaNew.getInformacaoMdtoColetaes().add(infoMedicamento);
						}
					}
			}
			informacaoColetaDAO.flush();
			informacaoColetaNew.setId(copiaInformacaoColetaNew.getId());
		}
		return informacaoColetaNew;
	}

	private AelInformacaoColeta cloneAelInformacaoColeta(AelInformacaoColeta informacaoColetaNew, AelSolicitacaoExames solicitacaoExame) {
		AelInformacaoColeta copiaInformacaoColetaNew = null;
		if (informacaoColetaNew != null && solicitacaoExame != null) {
			copiaInformacaoColetaNew = new AelInformacaoColeta();
			AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId(solicitacaoExame.getSeq(), INFORMACAO_COLETA_SEQP_INICIAL);
			copiaInformacaoColetaNew.setId(informacaoColetaId);
			copiaInformacaoColetaNew.setSolicitacaoExame(solicitacaoExame);
			copiaInformacaoColetaNew.setCumpriuJejum(informacaoColetaNew.getCumpriuJejum());
			copiaInformacaoColetaNew.setJejumRealizado(informacaoColetaNew.getJejumRealizado());
			copiaInformacaoColetaNew.setDocumento(informacaoColetaNew.getDocumento());
			copiaInformacaoColetaNew.setLocalColeta(informacaoColetaNew.getLocalColeta());
			copiaInformacaoColetaNew.setTipoAcesso(informacaoColetaNew.getTipoAcesso());
			copiaInformacaoColetaNew.setInfMenstruacao(informacaoColetaNew.getInfMenstruacao());
			copiaInformacaoColetaNew.setDtUltMenstruacao(informacaoColetaNew.getDtUltMenstruacao());
			copiaInformacaoColetaNew.setInformacoesAdicionais(informacaoColetaNew.getInformacoesAdicionais());
			copiaInformacaoColetaNew.setInfMedicacao(informacaoColetaNew.getInfMedicacao());
		}
		return copiaInformacaoColetaNew;
	}


	public AelInformacaoColeta obterInformacaoColeta(Integer soeSeq) {
		AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId(soeSeq, INFORMACAO_COLETA_SEQP_INICIAL);
		return  getAelInformacaoColetaDAO().obterPorChavePrimaria(informacaoColetaId, true, AelInformacaoColeta.Fields.INFORMACAO_MDTO_COLETAES);
	}
	
	public AelInformacaoColeta obterInformacaoColetaMascara(AelInformacaoColetaId informacaoColetaId) {
		return  getAelInformacaoColetaDAO().obterPorChavePrimaria(informacaoColetaId, true, AelInformacaoColeta.Fields.INFORMACAO_MDTO_COLETAES);
	}	

	public AelInformacaoColetaHist obterInformacaoColetaMascara(AelInformacaoColetaHistId informacaoColetaId) {
		return  aelInformacaoColetaHistDAO.obterPorChavePrimaria(informacaoColetaId, true, AelInformacaoColetaHist.Fields.INFORMACAO_MDTO_COLETAES);
	}		
	
	public void removerInformacaoMdtoColeta(AelInformacaoMdtoColeta informacaoMdtoColeta) throws ApplicationBusinessException {
		AelInformacaoMdtoColetaId informacaoMdtoColetaId = informacaoMdtoColeta.getId();
		if (informacaoMdtoColetaId != null && informacaoMdtoColetaId.getIclSeqp() != null && informacaoMdtoColetaId.getIclSoeSeq() != null
				&& informacaoMdtoColetaId.getSeqp() != null) {
			AelInformacaoMdtoColeta infoMdtoColeta = getAelInformacaoMdtoColetaDAO().obterPorChavePrimaria(informacaoMdtoColetaId);
			if (infoMdtoColeta != null) {
				getInformacaoMdtoColetaRN().removerInformacaoMdtoColeta(infoMdtoColeta);	
			}
		}
	}

	protected AelInformacaoColetaDAO getAelInformacaoColetaDAO() {
		return aelInformacaoColetaDAO;
	}

	protected AelInformacaoMdtoColetaDAO getAelInformacaoMdtoColetaDAO() {
		return aelInformacaoMdtoColetaDAO;
	}

	protected InformacaoColetaRN getInformacaoColetaRN() {
		return informacaoColetaRN;
	}

	protected InformacaoMdtoColetaRN getInformacaoMdtoColetaRN() {
		return informacaoMdtoColetaRN;
	}





}
