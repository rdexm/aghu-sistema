package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoNotaSalaDAO;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSala;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSalaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para cadastro de equipamentos para a nota de sala. 
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class EquipamentosNotaSalaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EquipamentosNotaSalaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcEquipamentoNotaSalaDAO mbcEquipamentoNotaSalaDAO;

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;


	private static final long serialVersionUID = -9001235819703398140L;

	private enum EquipamentosNotaSalaRNExceptionCode implements BusinessExceptionCode {
		MBC_00249, MSG_EQUIPAMENTO_NOTA_SALA_JA_ADICIONADO, MSG_ORDEM_IMP_EQUIPAMENTO_NOTA_SALA;
	}

	public void persistirEquipamentoNotaDeSala(MbcEquipamentoNotaSala equipamentoNotaSala) throws ApplicationBusinessException {
		
		if (equipamentoNotaSala.getId() == null) {
			
			this.executarAntesInserir(equipamentoNotaSala);
			
			this.getMbcEquipamentoNotaSalaDAO().persistir(equipamentoNotaSala);
			this.getMbcEquipamentoNotaSalaDAO().flush();

		} else {
			
			executarAntesAlterar(equipamentoNotaSala);
			this.getMbcEquipamentoNotaSalaDAO().atualizar(equipamentoNotaSala);
			this.getMbcEquipamentoNotaSalaDAO().flush();
			
		}
		
	}
	
	public void excluirEquipamentoNotaDeSala(MbcEquipamentoNotaSalaId equipamentoNotaSalaId){
		MbcEquipamentoNotaSala MbcEquipNotaSala = getMbcEquipamentoNotaSalaDAO().obterPorChavePrimaria(equipamentoNotaSalaId);

		this.getMbcEquipamentoNotaSalaDAO().remover(MbcEquipNotaSala);
		this.getMbcEquipamentoNotaSalaDAO().flush();
	}

	/**
	 * ORADB MBCT_EPN_BRI
	 * @param equipamentoNotaSala
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesInserir(MbcEquipamentoNotaSala equipamentoNotaSala) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		equipamentoNotaSala.setCriadoEm(new Date());
		equipamentoNotaSala.setRapServidores(servidorLogado);
		
		verificaSituacaoEquipamento(equipamentoNotaSala);
		verificaEquipamentoJaCadastradao(equipamentoNotaSala);
		
		MbcEquipamentoNotaSalaId equipamentoId = new MbcEquipamentoNotaSalaId();
		equipamentoId.setNoaUnfSeq(equipamentoNotaSala.getMbcUnidadeNotaSala().getId().getUnfSeq());
		equipamentoId.setNoaSeqp(equipamentoNotaSala.getMbcUnidadeNotaSala().getId().getSeqp());
		equipamentoId.setEuuSeq(equipamentoNotaSala.getMbcEquipamentoCirurgico().getSeq());
		equipamentoNotaSala.setId(equipamentoId);

	}
	
	/**
	 * ORADB mbck_epn_rn.rn_epnp_ver_eqp_cirg
	 * @param equipamentoNotaSala
	 * @throws ApplicationBusinessException 
	 */
	protected void verificaSituacaoEquipamento(MbcEquipamentoNotaSala equipamentoNotaSala) throws ApplicationBusinessException{
		MbcEquipamentoCirurgico equCir = getMbcEquipamentoCirurgicoDAO().obterOriginal(equipamentoNotaSala.getMbcEquipamentoCirurgico().getSeq());
		if(equCir != null && !equCir.getSituacao().isAtivo()){
			throw new ApplicationBusinessException(EquipamentosNotaSalaRNExceptionCode.MBC_00249);
		}
	}
	
	protected void verificaEquipamentoJaCadastradao(MbcEquipamentoNotaSala equipamentoNotaSala) throws ApplicationBusinessException{
	
		MbcEquipamentoNotaSalaId equpId = new MbcEquipamentoNotaSalaId();
		equpId.setNoaUnfSeq(equipamentoNotaSala.getMbcUnidadeNotaSala().getId().getUnfSeq());
		equpId.setNoaSeqp(equipamentoNotaSala.getMbcUnidadeNotaSala().getId().getSeqp());
		equpId.setEuuSeq(equipamentoNotaSala.getMbcEquipamentoCirurgico().getSeq());

		MbcEquipamentoNotaSala equipamentoNotaSalaExistente = getMbcEquipamentoNotaSalaDAO().obterPorChavePrimaria(equpId);
		if(equipamentoNotaSalaExistente != null){
			throw new ApplicationBusinessException(EquipamentosNotaSalaRNExceptionCode.MSG_EQUIPAMENTO_NOTA_SALA_JA_ADICIONADO);
		}
	}

	protected void executarAntesAlterar(MbcEquipamentoNotaSala equipamentoNotaSala) throws ApplicationBusinessException{
		if(equipamentoNotaSala.getOrdemImp()==null){
			throw new ApplicationBusinessException(EquipamentosNotaSalaRNExceptionCode.MSG_ORDEM_IMP_EQUIPAMENTO_NOTA_SALA);
		}		
	}

	protected MbcEquipamentoNotaSalaDAO getMbcEquipamentoNotaSalaDAO(){
		return mbcEquipamentoNotaSalaDAO;
	}

	protected MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO(){
		return mbcEquipamentoCirurgicoDAO;
	}
}