package br.gov.mec.aghu.blococirurgico.agendamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.agendamento.business.MbcDescricaoPadraoRN.DescricaoPadraoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoPadraoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoPadraoJnDAO;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;
import br.gov.mec.aghu.model.MbcDescricaoPadraoJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcDescricaoPadraoRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MbcDescricaoPadraoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoPadraoJnDAO mbcDescricaoPadraoJnDAO;

	@Inject
	private MbcDescricaoPadraoDAO mbcDescricaoPadraoDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2102595629648868998L;
	protected enum DescricaoPadraoRNExceptionCode implements BusinessExceptionCode {
		MBC_00667,MBC_00683,REGISTRO_NULO_BUSCA;
	}
	
	
	/** MBCT_DTP_BRI
	 * 
	 * 
	 * 
	 **/
	public void inserir(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		preInserir(descricaoPadrao);
		getMbcDescricaoPadraoDAO().persistir(descricaoPadrao);
	}
	
	
	public void atualizar(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		preAtualizar(descricaoPadrao);
		getMbcDescricaoPadraoDAO().merge(descricaoPadrao);
		posAtualizar(descricaoPadrao);
	}
	
	/**
	 * 
	 * @throws BaseException 
	 * @ORADB MBCT_DTP_BRI
	 * 1- Atualiza CRIADO_EM para data atual;
	 * 2- Verifica Especialidade está ativa
	 * 3- Verifica Procedimento está ativo
	 * 4- Atualiza Servidor
	 * 
	 **/
	public void preInserir(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		descricaoPadrao.setCriadoEm(new Date());
		this.verificaEspecialidadeAtiva(descricaoPadrao);
		this.verificaProcedimentoAtivo(descricaoPadrao);
		descricaoPadrao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}
	
	
	/**
	 * 
	 * @throws BaseException 
	 * @ORADB MBCT_DTP_BRU
	 * 1- Verifica Especialidade está ativa
	 * 2- Verifica Procedimento está ativo
	 * 3 - Testa se registro está na base
	 * 4- Atualiza Servidor
	 * 
	 **/
	public void preAtualizar(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		this.verificaEspecialidadeAtiva(descricaoPadrao);
		this.verificaProcedimentoAtivo(descricaoPadrao);
		this.obterPorChavePrimaria(descricaoPadrao.getId());
		descricaoPadrao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}
	
	
	public MbcDescricaoPadrao obterPorChavePrimaria(MbcDescricaoPadraoId id) throws BaseException {
		MbcDescricaoPadrao entidade = getMbcDescricaoPadraoDAO().obterOriginal(id);
		if(entidade == null) {
			throw new ApplicationBusinessException(DescricaoPadraoRNExceptionCode.REGISTRO_NULO_BUSCA);
		}
		return entidade;
	}
	
	public void verificaEspecialidadeAtiva(MbcDescricaoPadrao descricaoPadrao)throws BaseException {
		if(!descricaoPadrao.getAghEspecialidades().getIndSituacao().isAtivo()){
			throw new ApplicationBusinessException(DescricaoPadraoRNExceptionCode.MBC_00667);
		}
	}
	
	public void verificaProcedimentoAtivo(MbcDescricaoPadrao descricaoPadrao)throws BaseException {
		if(!descricaoPadrao.getMbcProcedimentoCirurgicos().getIndSituacao().isAtivo()){
			throw new ApplicationBusinessException(DescricaoPadraoRNExceptionCode.MBC_00683);
		}
	}
	
	
	
	/**
	 * 
	 * @throws BaseException 
	 * @ORADB MBCT_DTP_ARU
	 * 1- se houve atualização inserir registro na journal
	 * 
	 **/
	public void posAtualizar(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		MbcDescricaoPadrao descricaoPadraoOriginal = getMbcDescricaoPadraoDAO().obterOriginal(descricaoPadrao);
		if (CoreUtil.modificados(descricaoPadrao.getId(),descricaoPadraoOriginal.getId())
				|| CoreUtil.modificados(descricaoPadrao.getAghEspecialidades(), descricaoPadraoOriginal.getAghEspecialidades())
				|| CoreUtil.modificados(descricaoPadrao.getDescricaoTecPadrao(), descricaoPadraoOriginal.getDescricaoTecPadrao())
				|| CoreUtil.modificados(descricaoPadrao.getMbcProcedimentoCirurgicos(), descricaoPadraoOriginal.getMbcProcedimentoCirurgicos())
				|| CoreUtil.modificados(descricaoPadrao.getTitulo(), descricaoPadraoOriginal.getTitulo())){	
			this.inserirMbcDescricaoPadraoJn(descricaoPadraoOriginal, DominioOperacoesJournal.UPD);
		}
	}
	
	
	
	public void inserirMbcDescricaoPadraoJn(MbcDescricaoPadrao original, DominioOperacoesJournal op) throws BaseException{
		MbcDescricaoPadraoJn jn = BaseJournalFactory.getBaseJournal(op, MbcDescricaoPadraoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		jn.setCriadoEm(original.getCriadoEm());
		jn.setDescricaoTecPadrao(original.getDescricaoTecPadrao());
		jn.setEspSeq(original.getId().getEspSeq());
		jn.setSeqp(original.getId().getSeqp());
		jn.setNomeUsuario(servidorLogadoFacade.obterServidorLogado().getUsuario());
		jn.setOperacao(op);
		jn.setTitulo(original.getTitulo());
		jn.setPciSeq(original.getMbcProcedimentoCirurgicos().getSeq());
        jn.setSerMatricula(original.getRapServidores().getId().getMatricula());
        jn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
        getMbcDescricaoPadraoJnDAO().persistir(jn);
	}
	
	
	
	
	
	public void remover(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		getMbcDescricaoPadraoDAO().remover(descricaoPadrao);
		posRemover(descricaoPadrao);
	}
	
	/**
	 * 
	 * @throws BaseException
	 * @ORADB MBCT_DTP_ARD
	 * 
	 * 1- inserir na journal pos remove
	 * 
	 */
	public void posRemover(MbcDescricaoPadrao descricaoPadrao) throws BaseException {
		this.inserirMbcDescricaoPadraoJn(descricaoPadrao, DominioOperacoesJournal.DEL);
	}
	
	
	private MbcDescricaoPadraoJnDAO getMbcDescricaoPadraoJnDAO() {
		return mbcDescricaoPadraoJnDAO;
	}
	
	private MbcDescricaoPadraoDAO getMbcDescricaoPadraoDAO() {
		return mbcDescricaoPadraoDAO;
	}
}
