package br.gov.mec.aghu.exames.coleta.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.AelExamesAmostraVO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AmostraExamesON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AmostraExamesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@Inject
private VAelSolicAtendsDAO vAelSolicAtendsDAO;

@EJB
private IExamesFacade examesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7653829103701035597L;
	
	public enum AmostraExamesONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_SOLICITACAO_NAO_ENCONTRADA, MENSAGEM_NENHUM_ITEM_EXAME_AMOSTRA, MENSAGEM_INFORMAR_SOLICITACAO_AO_INFORMAR_AMOSTRA, MENSAGEM_INFORMAR_SOLICITACAO_OU_PACIENTE,
		MENSAGEM_SELECIONAR_ITENS_SITUACAO_GERADA, MENSAGEM_SELECIONAR_ITENS_SITUACAO_COLETADA;
	} 
	
	/**
	 * Retorna os dados da solicitação de exames pelo código da solicitação.
	 * 
	 * @param soeSeq
	 * @throws ApplicationBusinessException
	 */
	public List<VAelSolicAtendsVO> obterSolicAtendsPorItemHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) throws ApplicationBusinessException {
		List<Object[]> resultadoPesquisa = this.getVAelSolicAtendsDAO().obterSolicAtendsPorItemHorarioAgendado(hedGaeUnfSeq,hedGaeSeqp,hedDthrAgenda);
		List<VAelSolicAtendsVO> listaSolicitacoes = new ArrayList<VAelSolicAtendsVO>();
		
		if(resultadoPesquisa!=null && !resultadoPesquisa.isEmpty()) {
			for(Object[] item : resultadoPesquisa) {
				VAelSolicAtendsVO solicitacaoVO = new VAelSolicAtendsVO();
				solicitacaoVO.setNumero((Integer)item[0]); //soeSeq
				solicitacaoVO.setConvenio((String)item[1]); 
				solicitacaoVO.setCspSeq((Short)item[2]); 
				solicitacaoVO.setOrigem(DominioOrigemAtendimento.getInstance((String)item[3]));
				solicitacaoVO.setCodPaciente((Integer)item[4]);
				solicitacaoVO.setProntuario((Integer)item[5]);
				solicitacaoVO.setUnfDescricao((String)item[6]); 
				solicitacaoVO.setQuarto((String)item[7]);
				solicitacaoVO.setLeito((String)item[8]);
				solicitacaoVO.setInformacoesClinicas((String)item[9]);		
				solicitacaoVO.setCspCnvCodigo((Short)item[10]);	
				listaSolicitacoes.add(solicitacaoVO);
			}
		}
		else {
			throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_ERRO_SOLICITACAO_NAO_ENCONTRADA);
		}
		
		return listaSolicitacoes;
	}
	
	/**
	 * Verifica situação da Amostra se é G (gerada) ou M (em coleta).
	 * 
	 * @param situacaoAmostra
	 */
	public Boolean verificaSituacaoAmostraGeradaOuEmColeta(DominioSituacaoAmostra situacaoAmostra) {
		Boolean verificaSituacaoAmostra = false;
		if(situacaoAmostra!=null) {
			if(situacaoAmostra.equals(DominioSituacaoAmostra.G) || situacaoAmostra.equals(DominioSituacaoAmostra.M) ) {
				verificaSituacaoAmostra = true;
			}
			else {
				verificaSituacaoAmostra = false;
			}
		}
		return verificaSituacaoAmostra;
	}
	
	/**
	 * Verifica situação da Amostra se é C,U,R,M e A.
	 * 
	 * @param situacaoAmostra
	 */
	public Boolean verificaSituacaoAmostraCURMA(DominioSituacaoAmostra situacaoAmostra) {
		Boolean verificaSituacaoAmostra = false;
		if(situacaoAmostra!=null) {
			if(situacaoAmostra.equals(DominioSituacaoAmostra.C) || situacaoAmostra.equals(DominioSituacaoAmostra.U) ||
					situacaoAmostra.equals(DominioSituacaoAmostra.R) || situacaoAmostra.equals(DominioSituacaoAmostra.M) ||
					situacaoAmostra.equals(DominioSituacaoAmostra.A)) {
				verificaSituacaoAmostra = true;
			}
			else {
				verificaSituacaoAmostra = false;
			}
		}
		return verificaSituacaoAmostra;
	}
	
	/**
	 * Atualiza situacao de coleta de amostra e itens exame da amostra que estão geradas.
	 * 
	 * @param amostra
	 * @throws BaseException 
	 */
	public void atualizarSituacaoExamesAmostra(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		List<AelAmostraItemExames> itemExamesAmostras = getExamesFacade()
			.buscarAelAmostraItemExamesPorAmostra(amostra.getId().getSoeSeq(), amostra.getId().getSeqp().intValue());
		
		if (itemExamesAmostras!=null) {
			for(AelAmostraItemExames exame : itemExamesAmostras) {
				if(exame.getSituacao().equals(DominioSituacaoAmostra.G)) {
					exame.setSituacao(DominioSituacaoAmostra.C);
					getExamesFacade().atualizarAelAmostraItemExames(exame, true, true, nomeMicrocomputador);
				}
			}
		} else {
			throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_NENHUM_ITEM_EXAME_AMOSTRA);
		}
	}
	
	/**
	 * Atualiza situacao de coleta de amostra e itens exame da amostra que estão coletadas.
	 * 
	 * @param amostra
	 * @throws BaseException 
	 */
	public void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		List<AelAmostraItemExames> itemExamesAmostras = getExamesFacade()
			.buscarAelAmostraItemExamesPorAmostra(amostra.getId().getSoeSeq(), amostra.getId().getSeqp().intValue());
		
		if (itemExamesAmostras!=null) {
			for(AelAmostraItemExames exame : itemExamesAmostras) {
				if(exame.getSituacao().equals(DominioSituacaoAmostra.C)) {
					exame.setSituacao(DominioSituacaoAmostra.G);
					getExamesFacade().atualizarAelAmostraItemExames(exame, true, true, nomeMicrocomputador);
				}
			}
		} else {
			throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_NENHUM_ITEM_EXAME_AMOSTRA);
		}
	}
	
	public void validarSolicitacaoPorAmostra(Integer soeSeq, Short amostraSeq) throws ApplicationBusinessException{
		if(soeSeq==null && amostraSeq!=null){
			throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_INFORMAR_SOLICITACAO_AO_INFORMAR_AMOSTRA);
		}
	}
	
	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPaciente(Integer soeq, Integer pacCodigo){
		List<Object[]> resultadoPesquisa = this.getVAelSolicAtendsDAO().pesquisarSolicitacaoPorPaciente(soeq, pacCodigo);
		List<VAelSolicAtendsVO> listaSolicitacoes = new ArrayList<VAelSolicAtendsVO>();
		
		if(resultadoPesquisa!=null && !resultadoPesquisa.isEmpty()) {
			for(Object[] item : resultadoPesquisa) {
				VAelSolicAtendsVO solicitacaoVO = new VAelSolicAtendsVO();
				solicitacaoVO.setNumero((Integer)item[0]); //soeSeq
				solicitacaoVO.setCspCnvCodigo((Short)item[1]); 
				solicitacaoVO.setCspSeq((Short)item[2]); 
				solicitacaoVO.setOrigem(DominioOrigemAtendimento.getInstance((String)item[3]));
				solicitacaoVO.setCodPaciente((Integer)item[4]);
				solicitacaoVO.setProntuario((Integer)item[5]);
				solicitacaoVO.setTipo((String)item[6]);
				solicitacaoVO.setUnfDescricao((String)item[7]);
				if(item[8]!=null){
					solicitacaoVO.setQuarto(item[8].toString());	
				}
				solicitacaoVO.setLeito((String)item[9]);
				solicitacaoVO.setInformacoesClinicas((String)item[10]);		
				listaSolicitacoes.add(solicitacaoVO);
			}
		}
		return listaSolicitacoes;
	}
	
	public void validarSolicitacaoPaciente(Integer soeSeq, Integer pacCodigo) throws ApplicationBusinessException{
		if(soeSeq == null && pacCodigo == null){
			throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_INFORMAR_SOLICITACAO_OU_PACIENTE);
		}
	}
	
	public List<AelExamesAmostraVO> obterAmostraItemExamesPorAmostra(Integer soeSeq, Short seqp) {
		List<AelExamesAmostraVO> listaExames = getAelAmostraItemExamesDAO().obterAmostraItemExamesPorAmostra(soeSeq, seqp);
		List<AelExamesAmostraVO> listaExamesRetorno = new ArrayList<AelExamesAmostraVO>();
		AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(soeSeq, seqp);
		List<AelItemHorarioAgendado> listaItens = this.getAelItemHorarioAgendadoDAO().obterPorItemSolicitacaoExame(itemSolicitacaoExame);
		if(listaItens==null || listaItens.size()==0){
			return listaExames;
		}
		for(AelItemHorarioAgendado itemHorarioAgendado: listaItens){
			for(AelExamesAmostraVO exame: listaExames){
				AelExamesAmostraVO exameAux =  new AelExamesAmostraVO();
				exameAux.setDescricaoUsual(exame.getDescricaoUsual());
				exameAux.setSeqp(exame.getSeqp());
				exameAux.setSituacao(exame.getSituacao());
				exameAux.setSoeSeq(exame.getSoeSeq());
				exameAux.setTipoColeta(exame.getTipoColeta());
				exameAux.setUnfSeq(exame.getUnfSeq());
				exameAux.setDthrAgenda(itemHorarioAgendado.getId().getHedDthrAgenda());
				exameAux.setSelecionado(false);
				listaExamesRetorno.add(exameAux);
			} 
		}
		return listaExamesRetorno;
	}
	
	public void coletarExame(AelAmostraItemExames amostraItemExame, String nomeMicrocomputador) throws BaseException{
		amostraItemExame.setSituacao(DominioSituacaoAmostra.C);
		this.getExamesFacade().atualizarAelAmostraItemExames(amostraItemExame, false, true, nomeMicrocomputador);
	}
	
	public void voltarExame(AelAmostraItemExames amostraItemExame, String nomeMicrocomputador) throws BaseException{
		amostraItemExame.setSituacao(DominioSituacaoAmostra.G);
		this.getExamesFacade().atualizarAelAmostraItemExames(amostraItemExame, false, true, nomeMicrocomputador);
	}
	
	public void validarColetaExames(Integer amoSeqp, List<AelExamesAmostraVO> listaExamesAmostra) throws ApplicationBusinessException {
		for(AelExamesAmostraVO exameAmostraVO: listaExamesAmostra){
			AelAmostraItemExamesId id = new AelAmostraItemExamesId();
			id.setAmoSeqp(amoSeqp);
			id.setAmoSoeSeq(exameAmostraVO.getSoeSeq());
			id.setIseSoeSeq(exameAmostraVO.getSoeSeq());
			id.setIseSeqp(exameAmostraVO.getSeqp());
			AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(id);
			if(amostraItemExame.getSituacao().equals(DominioSituacaoAmostra.C)){
				throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_SELECIONAR_ITENS_SITUACAO_GERADA);
			}
		}
	}
	
	public void validarVoltaExames(Integer amoSeqp, List<AelExamesAmostraVO> listaExamesAmostra) throws ApplicationBusinessException{
		for(AelExamesAmostraVO exameAmostraVO: listaExamesAmostra){
			AelAmostraItemExamesId id = new AelAmostraItemExamesId();
			id.setAmoSeqp(amoSeqp);
			id.setAmoSoeSeq(exameAmostraVO.getSoeSeq());
			id.setIseSoeSeq(exameAmostraVO.getSoeSeq());
			id.setIseSeqp(exameAmostraVO.getSeqp());
			AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(id);
			if(amostraItemExame.getSituacao().equals(DominioSituacaoAmostra.G)){
				throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_SELECIONAR_ITENS_SITUACAO_COLETADA);
			}
		}
	}
	
	public void validarColetaExame(List<AelAmostraExamesVO> listaItensAmostra) throws ApplicationBusinessException {
		for(AelAmostraExamesVO amostraExames : listaItensAmostra) {
			if(amostraExames.getSelecionado()) {
				AelAmostraItemExamesId amostraItemExamesId = new AelAmostraItemExamesId(amostraExames.getIseSoeSeq(), 
						amostraExames.getIseSeqp(), amostraExames.getAmoSoeSeq(), amostraExames.getAmoSeqp());
				AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(amostraItemExamesId);
				if(amostraItemExame.getSituacao().equals(DominioSituacaoAmostra.C)){
					throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_SELECIONAR_ITENS_SITUACAO_GERADA);
				}
			}
		}
	}
	
	public void validarVoltaExame(List<AelAmostraExamesVO> listaItensAmostra) throws ApplicationBusinessException {
		for(AelAmostraExamesVO amostraExames : listaItensAmostra) {
			if(amostraExames.getSelecionado()) {
				AelAmostraItemExamesId amostraItemExamesId = new AelAmostraItemExamesId(amostraExames.getIseSoeSeq(), 
						amostraExames.getIseSeqp(), amostraExames.getAmoSoeSeq(), amostraExames.getAmoSeqp());
				AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(amostraItemExamesId);
				if(amostraItemExame.getSituacao().equals(DominioSituacaoAmostra.G)) {
					throw new ApplicationBusinessException(AmostraExamesONExceptionCode.MENSAGEM_SELECIONAR_ITENS_SITUACAO_COLETADA);
				}
			}
		}
	}
	
	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPacienteEAmostra(Integer soeq, Integer pacCodigo, Short amostraSeq){
		List<VAelSolicAtendsVO> lista = this.getVAelSolicAtendsDAO().pesquisarSolicitacaoPorPacienteEAmostra(soeq, pacCodigo, amostraSeq); 
		return lista;
	}
	
	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	
}
