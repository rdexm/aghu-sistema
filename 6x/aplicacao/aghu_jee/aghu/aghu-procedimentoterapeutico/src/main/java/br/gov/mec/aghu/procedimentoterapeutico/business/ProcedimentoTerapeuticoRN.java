package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaJn;
import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.model.MptProtocoloAssociacao;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.model.MptIntercorrencia;
import br.gov.mec.aghu.model.MptIntercorrenciaJn;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MptProtocoloCuidadosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendaPrescricaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptParamCalculoDosesDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloAssociacaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCuidadosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloItemMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptVersaoProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HomologarProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptIntercorrenciaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptIntercorrenciaJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoIntercorrenciaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@Stateless
public class ProcedimentoTerapeuticoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProcedimentoTerapeuticoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private MptAgendaPrescricaoDAO mptAgendaPrescricaoDAO;
	
	@Inject
	private MptVersaoProtocoloSessaoDAO mptVersaoProtocoloSessaoDAO;
	
	@Inject
	private MptProtocoloAssociacaoDAO mptProtocoloAssociacaoDAO;
	
	@Inject
	private MptProtocoloSessaoDAO mptProtocoloSessaoDAO;

	@Inject
	private MptCaracteristicaDAO mptCaracteristicaDAO;

	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;

	@Inject
	private MptCaracteristicaJnDAO mptCaracteristicaJnDAO;
	
	@Inject
	private MptProtocoloMedicamentosDAO mptProtocoloMedicamentosDAO;

	@Inject
	private MptIntercorrenciaDAO mptIntercorrenciaDAO;
	
	@Inject
	private MptProtocoloItemMedicamentosDAO mptProtocoloItemMedicamentosDAO;
	
	@Inject
	private MptProtocoloMedicamentosDiaDAO mptProtocoloMedicamentosDiaDAO;
	
	@Inject
	private MptProtocoloCuidadosDAO mptProtocoloCuidadosDAO;
	
	@Inject
	private MptProtocoloCuidadosDiaDAO mptProtocoloCuidadosDiaDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MptParamCalculoDosesDAO mptParamCalculoDosesDAO;
	
@Inject
private MptProtocoloCicloDAO mptProtocoloCicloDAO;

@Inject
private MptTipoIntercorrenciaDAO mptTipoIntercorrenciaDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private MptIntercorrenciaJnDAO mptIntercorrenciaJnDAO;

@Inject
private MptSessaoDAO mptSessaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -67530138915014344L;
	
	private enum ProcedimentoTerapeuticoRNExceptionCode implements BusinessExceptionCode {
		EXISTE_VINCULO, INFORMAR_SIGLA, INFORMAR_DESCRICAO, SIGLA_EXISTE, DESCRICAO_EXISTE,NAO_E_PERMITIDO_CARACTER_ESPECIAL_HIFEN,DESCRICAO_GRANDE_DEMAIS, CAMPO_DESCRICAO_OBRIGATORIO,MSG_ERRO_HOMOLOGAR_PROTOCOLO,
		MSG_SUCESSO_HOMOLOGAR_PROTOCOLO, MSG_VERSAO_NAO_HOMOLOGADA, MSG_ASSUNTO_LIBERACAO_VERSAO_PROTOCOLO, MSG_LIBERACAO_VERSAO_PROTOCOLO, MSG_ASSUNTO_VERSAO_NAO_HOMOLOGADA;
	}

	public void atualizaMptAgendaPrescricao(MptAgendaPrescricao agendaPrescricao, boolean flush) throws BaseException {
		// TODO Implementar este método e implementar as triggers da tabela MPT_AGENDA_PRESCRICOES
		this.preAtualizaMptAgendaPrescricao(agendaPrescricao);
		
		MptAgendaPrescricaoDAO mptAgendaPrescricaoDAO = this.getMptAgendaPrescricaoDAO();
		mptAgendaPrescricaoDAO.atualizar(agendaPrescricao);
		if (flush) {
			mptAgendaPrescricaoDAO.flush();
		}
	}
	
	/** 
	 * ORADB MPTT_AGP_BRU
	 * 
	 * @param agendaPrescricao
	 */
	private void preAtualizaMptAgendaPrescricao(MptAgendaPrescricao agendaPrescricao) {
		// TODO Implementar esta trigger
	}

	protected MptAgendaPrescricaoDAO getMptAgendaPrescricaoDAO() {
		return mptAgendaPrescricaoDAO;
	}
	
	/**RN01 #46468 Recuperar lista**/
	public List<MptCaracteristica> recuperarListaDeCaracteristicas(MptTipoSessao tipoSessaoFiltro, String descricaoFiltro, DominioSituacao situacao){
		if(tipoSessaoFiltro != null || descricaoFiltro != null && !descricaoFiltro.isEmpty() || situacao != null){
			return mptCaracteristicaDAO.obterCaracteristicasPorFiltro(tipoSessaoFiltro, descricaoFiltro, situacao);
		}else{
			return mptCaracteristicaDAO.obterCaracteristicasAtivasDescSeqSigla();
		}
	}
	
	public void salvarCaracteristica(MptCaracteristica caracteristica, String usuarioLogado) throws ApplicationBusinessException{
		if(caracteristica.getSeq() != null){
			atualizarCaracteristica(caracteristica, usuarioLogado);
		}else{
			persistirCaracteristica(caracteristica);
		}
		mptCaracteristicaDAO.flush();
	}

	private void persistirCaracteristica(MptCaracteristica caracteristica) throws ApplicationBusinessException {
		caracteristica.setCriadoEm(new Date());
		mptCaracteristicaDAO.persistir(caracteristica);
	}

	private void atualizarCaracteristica(MptCaracteristica caracteristica, String usuarioLogado) throws ApplicationBusinessException {
		MptCaracteristica velha = mptCaracteristicaDAO.obterOriginal(caracteristica.getSeq());
		mptCaracteristicaDAO.atualizar(caracteristica);
		MptCaracteristicaJn caracteristicaJn = new MptCaracteristicaJn();
		caracteristicaJn.setSeq(velha.getSeq());
		caracteristicaJn.setSigla(velha.getSigla());
		caracteristicaJn.setDescricao(velha.getDescricao());
		caracteristicaJn.setIndSituacao(velha.getIndSituacao());
		caracteristicaJn.setNomeUsuario(usuarioLogado);
		caracteristicaJn.setOperacao(DominioOperacoesJournal.UPD);
		mptCaracteristicaJnDAO.persistir(caracteristicaJn);
		mptCaracteristicaJnDAO.flush();
	}
	
	public List<String> validarPersistenciaCaracteristica(MptCaracteristica caracteristica){
		List<String> validacoes = new ArrayList<String>();
		//RN05 #46468
		if(caracteristica.getSigla() == null || caracteristica.getSigla().isEmpty()){
			validacoes.add("INFORMAR_SIGLA");
		}
		//RN05 #46468
		if(caracteristica.getDescricao() == null || caracteristica.getDescricao().isEmpty()){
			validacoes.add("INFORMAR_DESCRICAO");
		}
		//RN06 #46468
		if(mptCaracteristicaDAO.verificarSigla(caracteristica.getSigla())){
			validacoes.add("SIGLA_EXISTE");
		}
		//RN06 #46468
		if(mptCaracteristicaDAO.verificarDescricao(caracteristica.getDescricao())){
			validacoes.add("DESCRICAO_EXISTE");
		}
		if(caracteristica.getSigla() != null && !caracteristica.getSigla().isEmpty() && caracteristica.getSigla().contains("-")){
			validacoes.add("NAO_E_PERMITIDO_CARACTER_ESPECIAL_HIFEN");
		}
		if(caracteristica.getDescricao() != null && !caracteristica.getDescricao().isEmpty() && caracteristica.getDescricao().contains("-")){
			validacoes.add("NAO_E_PERMITIDO_CARACTER_ESPECIAL_HIFEN");
		}
		return validacoes;
	}
	
	/**RN03 #46468**/
	public void validarInativacaoStatusCaracteristica(MptCaracteristica caracteristica, Boolean situacao)throws ApplicationBusinessException{
		if(!situacao && mptCaracteristicaTipoSessaoDAO.existeVinculoEntreCaracteristicaTipoSessao(caracteristica.getSeq())){
			throw new ApplicationBusinessException(ProcedimentoTerapeuticoRNExceptionCode.EXISTE_VINCULO, Severity.ERROR);
		}
	}
	
	/**
	 * #44271 - Pesquisar protocolos ativos
	 * @param filtro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return lista de protocolos.
	 */
	public List<ProtocoloVO> pesquisarProtocolosAtivos(ProtocoloVO filtro, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		List<ProtocoloVO> listaProtocolos = mptVersaoProtocoloSessaoDAO.pesquisarProtocolosAtivos(filtro, firstResult, maxResults, orderProperty, asc);
		String tituloAnterior = "";
		for (ProtocoloVO protocoloVO: listaProtocolos) {
			if (!protocoloVO.getTituloProtocoloSessao().equals(tituloAnterior)) {
				tituloAnterior = protocoloVO.getTituloProtocoloSessao();
				protocoloVO.setRepetido(false);
			} else {
				protocoloVO.setRepetido(true);
			}		
		}
		
		return listaProtocolos;
	}
	
	/**
	 * Contar quantidade da pesquisa de protocolos ativos.
	 * @param filtro
	 * @return quantidade.
	 */
	public Long contarPesquisarProtocolosAtivos(ProtocoloVO filtro) {
		return mptVersaoProtocoloSessaoDAO.contarPesquisarProtocolosAtivos(filtro);
	}
	
	/**
	 * #44271 - RN04
	 * @param itemExclusao
	 */
	public void excluirVersaoProtocolo(ProtocoloVO itemExclusao) {
		List<MptVersaoProtocoloSessao> listaVersoes = mptVersaoProtocoloSessaoDAO.pesquisarVersoesProtocolo(itemExclusao.getSeqProtocoloSessao());
		if (listaVersoes != null && !listaVersoes.isEmpty() && listaVersoes.size() == 1) {			
			List<MptProtocoloAssociacao> listaProtocolosAssociacao = mptProtocoloAssociacaoDAO.obterProtocolosAssociacaoPorProtocolo(itemExclusao.getSeqProtocoloSessao());
			if (listaProtocolosAssociacao != null && !listaProtocolosAssociacao.isEmpty()) {
				for (MptProtocoloAssociacao protocoloAssociacao: listaProtocolosAssociacao) {
					mptProtocoloAssociacaoDAO.remover(protocoloAssociacao);
				}
			}
			
			mptVersaoProtocoloSessaoDAO.remover(listaVersoes.get(0));
			
			mptProtocoloSessaoDAO.removerPorId(itemExclusao.getSeqProtocoloSessao());
			
		} else {
			mptVersaoProtocoloSessaoDAO.removerPorId(itemExclusao.getSeqVersaoProtocoloSessao());
		}
	}
	
	public void excluirTratamento(List<ProtocoloMedicamentoSolucaoCuidadoVO> lista, ProtocoloMedicamentoSolucaoCuidadoVO tratamento) {
		List<MptProtocoloMedicamentosDia> listaMdtosDia = new ArrayList<MptProtocoloMedicamentosDia>();
		listaMdtosDia = this.mptProtocoloMedicamentosDiaDAO.verificarExisteDiaMarcadoParaProtocolo(tratamento.getPtmSeq());
		List<MptParamCalculoDoses> itemParamDose = mptParamCalculoDosesDAO.obterListaDoseMdtosPorSeqMdto(tratamento.getSeqItemProtocoloMdtos());
		
		
		if(!listaMdtosDia.isEmpty()){
			for(MptProtocoloMedicamentosDia item : listaMdtosDia){
				this.mptProtocoloMedicamentosDiaDAO.removerPorId(item.getSeq());
			}
		}
		if(!itemParamDose.isEmpty()){
			for(MptParamCalculoDoses item : itemParamDose){
				mptParamCalculoDosesDAO.remover(item);
			}
		}
				
		this.mptProtocoloItemMedicamentosDAO.removerPorId(tratamento.getSeqItemProtocoloMdtos());
		this.mptProtocoloMedicamentosDAO.removerPorId(tratamento.getPtmSeq());
		atualizarCampoOrdem(lista, tratamento.getOrdem());
	}
	
	public void fazerCheckCelula(ProtocoloMedicamentoSolucaoCuidadoVO vo, Short dia) {
		
		if (vo.getPtmSeq() != null) {		
			MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia = mptProtocoloMedicamentosDiaDAO.obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(vo.getPtmSeq(), dia);
			
			if (mptProtocoloMedicamentosDia == null) {
				MptProtocoloMedicamentos mptProtocoloMedicamentos = mptProtocoloMedicamentosDAO.obterPorChavePrimaria(vo.getPtmSeq());
				Hibernate.initialize(mptProtocoloMedicamentos.getVersaoProtocoloSessao());
				Hibernate.initialize(mptProtocoloMedicamentos.getTfqSeq());
				Hibernate.initialize(mptProtocoloMedicamentos.getVadSigla());
				
				mptProtocoloMedicamentosDia = new MptProtocoloMedicamentosDia();
				mptProtocoloMedicamentosDia.setDescricao(mptProtocoloMedicamentos.getDescricao());
				mptProtocoloMedicamentosDia.setVersaoProtocoloSessao(mptProtocoloMedicamentos.getVersaoProtocoloSessao());
				mptProtocoloMedicamentosDia.setTfqSeq(mptProtocoloMedicamentos.getTfqSeq().getSeq());
				mptProtocoloMedicamentosDia.setVadSigla(mptProtocoloMedicamentos.getVadSigla().getSigla());
				mptProtocoloMedicamentosDia.setTvaSeq(mptProtocoloMedicamentos.getTvaSeq());
				mptProtocoloMedicamentosDia.setIndSeNecessario(mptProtocoloMedicamentos.getIndSeNecessario());
				mptProtocoloMedicamentosDia.setIndSolucao(mptProtocoloMedicamentos.getIndSolucao());
				mptProtocoloMedicamentosDia.setFrequencia(mptProtocoloMedicamentos.getFrequencia());
				mptProtocoloMedicamentosDia.setQtdHorasCorrer(mptProtocoloMedicamentos.getQtdeHorasCorrer());
				mptProtocoloMedicamentosDia.setUnidHorasCorrer(mptProtocoloMedicamentos.getUnidHorasCorrer());
				mptProtocoloMedicamentosDia.setIndBombaInfusao(mptProtocoloMedicamentos.getIndBombaInfusao());
				mptProtocoloMedicamentosDia.setIndInfusorPortatil(mptProtocoloMedicamentos.getIndInfusorPortatil());
				mptProtocoloMedicamentosDia.setComplemento(mptProtocoloMedicamentos.getComplemento());								
				mptProtocoloMedicamentosDia.setObservacao(mptProtocoloMedicamentos.getObservacao());
				mptProtocoloMedicamentosDia.setIndUsoDomiciliar(mptProtocoloMedicamentos.getIndDomiciliar());
				mptProtocoloMedicamentosDia.setDiasUsoDomiciliar(mptProtocoloMedicamentos.getDiasDeUsoDomiciliar());
				mptProtocoloMedicamentosDia.setGotejo(mptProtocoloMedicamentos.getGotejo());
				mptProtocoloMedicamentosDia.setVolumeMl(mptProtocoloMedicamentos.getVolumeMl());
				mptProtocoloMedicamentosDia.setModificado(Boolean.FALSE);
				mptProtocoloMedicamentosDia.setProtocoloMedicamentos(mptProtocoloMedicamentos);
				mptProtocoloMedicamentosDia.setDia(dia);
				mptProtocoloMedicamentosDia.setCriadoEm(new Date());
				
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
				
				mptProtocoloMedicamentosDia.setServidor(servidorLogado);				
				
				mptProtocoloMedicamentosDiaDAO.persistir(mptProtocoloMedicamentosDia);
			}
		} else if (vo.getPcuSeq() != null) {
			MptProtocoloCuidadosDia mptProtocoloCuidadosDia = mptProtocoloCuidadosDiaDAO.obterProtocoloCuidadosDiaPorPcuSeqDiaCompleto(vo.getPcuSeq(), dia);
			
			if (mptProtocoloCuidadosDia == null) {
				MptProtocoloCuidados mptProtocoloCuidados = mptProtocoloCuidadosDAO.obterPorChavePrimaria(vo.getPcuSeq());
				Hibernate.initialize(mptProtocoloCuidados.getVersaoProtocoloSessao());
				Hibernate.initialize(mptProtocoloCuidados.getTipoFrequenciaAprazamento());
				Hibernate.initialize(mptProtocoloCuidados.getCuidadoUsual());
				
				mptProtocoloCuidadosDia = new MptProtocoloCuidadosDia();
				mptProtocoloCuidadosDia.setVersaoProtocoloSessao(mptProtocoloCuidados.getVersaoProtocoloSessao());
				mptProtocoloCuidadosDia.setTfqSeq(mptProtocoloCuidados.getTipoFrequenciaAprazamento().getSeq());
				mptProtocoloCuidadosDia.setCduSeq(mptProtocoloCuidados.getCuidadoUsual().getSeq());
				mptProtocoloCuidadosDia.setFrequencia(mptProtocoloCuidados.getFrequencia());
				mptProtocoloCuidadosDia.setComplemento(mptProtocoloCuidados.getComplemento());
				mptProtocoloCuidadosDia.setTempo(mptProtocoloCuidados.getTempo());
				mptProtocoloCuidadosDia.setModificado(Boolean.FALSE);
				mptProtocoloCuidadosDia.setProtocoloCuidados(mptProtocoloCuidados);
				mptProtocoloCuidadosDia.setDia(dia);
				mptProtocoloCuidadosDia.setCriadoEm(new Date());
				
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
				
				mptProtocoloCuidadosDia.setServidor(servidorLogado);
				
				mptProtocoloCuidadosDiaDAO.persistir(mptProtocoloCuidadosDia);
			}
		}
	}	
	
	public void atualizarTodosDiasModificadosMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia){
		for(MptProtocoloMedicamentosDia item : listaMptProtocoloMedicamentosDia){
			if(item.getModificado()){
				
				item.setDescricao(mptProtocoloMedicamentos.getDescricao());
				item.setVersaoProtocoloSessao(mptProtocoloMedicamentos.getVersaoProtocoloSessao());
				item.setTfqSeq(mptProtocoloMedicamentos.getTfqSeq().getSeq());
				item.setVadSigla(mptProtocoloMedicamentos.getVadSigla().getSigla());
				item.setTvaSeq(mptProtocoloMedicamentos.getTvaSeq());
				item.setIndSeNecessario(mptProtocoloMedicamentos.getIndSeNecessario());
				item.setIndSolucao(mptProtocoloMedicamentos.getIndSolucao());
				item.setFrequencia(mptProtocoloMedicamentos.getFrequencia());
				item.setQtdHorasCorrer(mptProtocoloMedicamentos.getQtdeHorasCorrer());
				item.setUnidHorasCorrer(mptProtocoloMedicamentos.getUnidHorasCorrer());
				item.setIndBombaInfusao(mptProtocoloMedicamentos.getIndBombaInfusao());
				item.setIndInfusorPortatil(mptProtocoloMedicamentos.getIndInfusorPortatil());
				item.setComplemento(mptProtocoloMedicamentos.getComplemento());
				item.setObservacao(mptProtocoloMedicamentos.getObservacao());
				item.setIndUsoDomiciliar(mptProtocoloMedicamentos.getIndDomiciliar());
				item.setDiasUsoDomiciliar(mptProtocoloMedicamentos.getDiasDeUsoDomiciliar());
				item.setGotejo(mptProtocoloMedicamentos.getGotejo());
				item.setVolumeMl(mptProtocoloMedicamentos.getVolumeMl());
				item.setModificado(Boolean.FALSE);
				item.setProtocoloMedicamentos(mptProtocoloMedicamentos);
								
				mptProtocoloMedicamentosDiaDAO.atualizar(item);
			}
		}
	}
	
	public void atualizarDiasModificadosMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, List<MptProtocoloMedicamentosDia> listaMptProtocoloMedicamentosDia){
		for(MptProtocoloMedicamentosDia item : listaMptProtocoloMedicamentosDia){
			if(!item.getModificado()){
				
				item.setDescricao(mptProtocoloMedicamentos.getDescricao());
				item.setVersaoProtocoloSessao(mptProtocoloMedicamentos.getVersaoProtocoloSessao());
				item.setTfqSeq(mptProtocoloMedicamentos.getTfqSeq().getSeq());
				item.setVadSigla(mptProtocoloMedicamentos.getVadSigla().getSigla());
				item.setTvaSeq(mptProtocoloMedicamentos.getTvaSeq());
				item.setIndSeNecessario(mptProtocoloMedicamentos.getIndSeNecessario());
				item.setIndSolucao(mptProtocoloMedicamentos.getIndSolucao());
				item.setFrequencia(mptProtocoloMedicamentos.getFrequencia());
				item.setQtdHorasCorrer(mptProtocoloMedicamentos.getQtdeHorasCorrer());
				item.setUnidHorasCorrer(mptProtocoloMedicamentos.getUnidHorasCorrer());
				item.setIndBombaInfusao(mptProtocoloMedicamentos.getIndBombaInfusao());
				item.setIndInfusorPortatil(mptProtocoloMedicamentos.getIndInfusorPortatil());
				item.setComplemento(mptProtocoloMedicamentos.getComplemento());
				item.setObservacao(mptProtocoloMedicamentos.getObservacao());
				item.setIndUsoDomiciliar(mptProtocoloMedicamentos.getIndDomiciliar());
				item.setDiasUsoDomiciliar(mptProtocoloMedicamentos.getDiasDeUsoDomiciliar());
				item.setGotejo(mptProtocoloMedicamentos.getGotejo());
				item.setVolumeMl(mptProtocoloMedicamentos.getVolumeMl());
				item.setProtocoloMedicamentos(mptProtocoloMedicamentos);
				
				mptProtocoloMedicamentosDiaDAO.atualizar(item);
			}
		}
	}
	
	public void atualizarDiaModificadoMedicamentos(MptProtocoloMedicamentos mptProtocoloMedicamentos, MptProtocoloMedicamentosDia mptProtocoloMedicamentosDia){
		
		mptProtocoloMedicamentosDia.setDescricao(mptProtocoloMedicamentos.getDescricao());
		mptProtocoloMedicamentosDia.setVersaoProtocoloSessao(mptProtocoloMedicamentos.getVersaoProtocoloSessao());
		mptProtocoloMedicamentosDia.setTfqSeq(mptProtocoloMedicamentos.getTfqSeq().getSeq());
		mptProtocoloMedicamentosDia.setVadSigla(mptProtocoloMedicamentos.getVadSigla().getSigla());
		mptProtocoloMedicamentosDia.setTvaSeq(mptProtocoloMedicamentos.getTvaSeq());
		mptProtocoloMedicamentosDia.setIndSeNecessario(mptProtocoloMedicamentos.getIndSeNecessario());
		mptProtocoloMedicamentosDia.setIndSolucao(mptProtocoloMedicamentos.getIndSolucao());
		mptProtocoloMedicamentosDia.setFrequencia(mptProtocoloMedicamentos.getFrequencia());
		mptProtocoloMedicamentosDia.setQtdHorasCorrer(mptProtocoloMedicamentos.getQtdeHorasCorrer());
		mptProtocoloMedicamentosDia.setUnidHorasCorrer(mptProtocoloMedicamentos.getUnidHorasCorrer());
		mptProtocoloMedicamentosDia.setIndBombaInfusao(mptProtocoloMedicamentos.getIndBombaInfusao());
		mptProtocoloMedicamentosDia.setIndInfusorPortatil(mptProtocoloMedicamentos.getIndInfusorPortatil());
		mptProtocoloMedicamentosDia.setComplemento(mptProtocoloMedicamentos.getComplemento());
		mptProtocoloMedicamentosDia.setObservacao(mptProtocoloMedicamentos.getObservacao());
		mptProtocoloMedicamentosDia.setIndUsoDomiciliar(mptProtocoloMedicamentos.getIndDomiciliar());
		mptProtocoloMedicamentosDia.setDiasUsoDomiciliar(mptProtocoloMedicamentos.getDiasDeUsoDomiciliar());
		mptProtocoloMedicamentosDia.setGotejo(mptProtocoloMedicamentos.getGotejo());
		mptProtocoloMedicamentosDia.setVolumeMl(mptProtocoloMedicamentos.getVolumeMl());
		mptProtocoloMedicamentosDia.setModificado(Boolean.TRUE);
		mptProtocoloMedicamentosDia.setProtocoloMedicamentos(mptProtocoloMedicamentos);
		
		mptProtocoloMedicamentosDiaDAO.atualizar(mptProtocoloMedicamentosDia);
		
	}
	
	public void atualizarCampoOrdem (List<ProtocoloMedicamentoSolucaoCuidadoVO> lista, Short ordem){

		MptProtocoloMedicamentos mptProtocoloMedicamentos = null;
		MptProtocoloCuidados mptProtocoloCuidados =  null;
		for (ProtocoloMedicamentoSolucaoCuidadoVO item : lista){			
			if(item.getOrdem() != null &&  ordem != null && item.getOrdem()  > ordem){
				if(item.getPtmSeq() != null){					
						Short novaOrdem = (short) (item.getOrdem() - Short.valueOf((short) 1));
						mptProtocoloMedicamentos = mptProtocoloMedicamentosDAO.obterPorChavePrimaria(item.getPtmSeq());
						if(mptProtocoloMedicamentos != null){
							mptProtocoloMedicamentos.setOrdem(novaOrdem);
							mptProtocoloMedicamentosDAO.atualizar(mptProtocoloMedicamentos);					
						}
				}else if(item.getPcuSeq() != null){
						Short novaOrdem = (short) (item.getOrdem() - Short.valueOf((short) 1));
						mptProtocoloCuidados = mptProtocoloCuidadosDAO.obterPorChavePrimaria(item.getPcuSeq());
						if(mptProtocoloCuidados != null){
							mptProtocoloCuidados.setOrdem(novaOrdem);
							mptProtocoloCuidadosDAO.atualizar(mptProtocoloCuidados);					
					}
				}
			}
		}
	}

	public void removerDiasCuidados(Integer seqCuidadoExclusao) {		 
		List<MptProtocoloCuidadosDia> diasRemocao = mptProtocoloCuidadosDiaDAO.verificarDiaCuidado(seqCuidadoExclusao);
		if(diasRemocao.size() > 0){
			for(MptProtocoloCuidadosDia item: diasRemocao){
				this.mptProtocoloCuidadosDiaDAO.removerPorId(item.getSeq());				
			}
		}
	}
	
	public MptProtocoloMedicamentos buscarSolucaoParaEdicao(Long codSolucao) {
		MptProtocoloMedicamentos mptProtocoloMedicamentos = mptProtocoloMedicamentosDAO.obterPorChavePrimaria(codSolucao);
		
		if (mptProtocoloMedicamentos != null) {
			Hibernate.initialize(mptProtocoloMedicamentos.getVadSigla());
			Hibernate.initialize(mptProtocoloMedicamentos.getTfqSeq());
			Hibernate.initialize(mptProtocoloMedicamentos.getTipoVelocAdministracoes());
		}
		
		return mptProtocoloMedicamentos;
	}
	
	/**#44279 - Realiza a gravação dos itens a serem homologados**/
	public void gravarHomologacaoProtocolo(Integer vpsSeq, String justificativa, DominioSituacaoProtocolo situacao, List<HomologarProtocoloVO> listaMedicamentosProtocolo) throws ApplicationBusinessException{
		if(vpsSeq != null && situacao != null){
			MptVersaoProtocoloSessao mptVersaoProtocoloSessao = mptVersaoProtocoloSessaoDAO.obterPorChavePrimaria(vpsSeq);
			if(mptVersaoProtocoloSessao !=  null){
				mptVersaoProtocoloSessao.setIndSituacao(situacao);
				if(StringUtils.isNotBlank(justificativa)){
					mptVersaoProtocoloSessao.setJustificativa(justificativa);
				}
				mptVersaoProtocoloSessaoDAO.atualizar(mptVersaoProtocoloSessao);
				this.flush();
			}else{
				throw new ApplicationBusinessException(ProcedimentoTerapeuticoRNExceptionCode.MSG_ERRO_HOMOLOGAR_PROTOCOLO, Severity.ERROR); 
			}
			//Persiste as alterações da lista de medicamentos do protocolo
			if(listaMedicamentosProtocolo != null && !listaMedicamentosProtocolo.isEmpty()){
				for (HomologarProtocoloVO itemMedicamentoProtocolo : listaMedicamentosProtocolo) {
					MptProtocoloMedicamentos mptProtocoloMedicamentos = mptProtocoloMedicamentosDAO.obterPorChavePrimaria(itemMedicamentoProtocolo.getPtmSeq());
					if(mptProtocoloMedicamentos != null){
						mptProtocoloMedicamentos.setIndNaoPermiteAlteracao(itemMedicamentoProtocolo.getIndPermiteAlteracao());
						mptProtocoloMedicamentosDAO.atualizar(mptProtocoloMedicamentos);
						this.flush();
					}
				}
			}
			notificarResponsavelProtocoloSobrePendenciaHomologacao(vpsSeq, situacao);
		}
	}
	
	/**#44279 (RN01) - Envia email e adiciona aviso a central de notificações do responsável do protocolo. Informando se o protocolo em questão foi liberado para o uso ou não.**/
	public void notificarResponsavelProtocoloSobrePendenciaHomologacao(Integer vpsSeq, DominioSituacaoProtocolo situacao) throws ApplicationBusinessException{
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = mptVersaoProtocoloSessaoDAO.obterPorChavePrimaria(vpsSeq);
		String conteudo = StringUtils.EMPTY;
		String assunto = StringUtils.EMPTY;
		String remetente = this.servidorLogadoFacade.obterServidorLogado().getEmail();
		String destinatario = StringUtils.EMPTY;
		String url = null;
		String descricaoAba = null;
		RapServidores servidorResponsavel = new RapServidores();
		//Monta o email
		if(situacao != null && mptVersaoProtocoloSessao != null){
			servidorResponsavel = mptVersaoProtocoloSessao.getServidorResponsavel();
			destinatario = servidorResponsavel.getEmail();
			String nomeProtocolo = mptVersaoProtocoloSessao.getProtocoloSessao().getTitulo();
			
			if(situacao.equals(DominioSituacaoProtocolo.I)){
				conteudo = super.getResourceBundleValue(ProcedimentoTerapeuticoRNExceptionCode.MSG_VERSAO_NAO_HOMOLOGADA.toString(), nomeProtocolo);
				assunto = super.getResourceBundleValue(ProcedimentoTerapeuticoRNExceptionCode.MSG_ASSUNTO_VERSAO_NAO_HOMOLOGADA.toString(), nomeProtocolo);
			}
			if(situacao.equals(DominioSituacaoProtocolo.L)){
				conteudo = super.getResourceBundleValue(ProcedimentoTerapeuticoRNExceptionCode.MSG_LIBERACAO_VERSAO_PROTOCOLO.toString(), nomeProtocolo);
				assunto = super.getResourceBundleValue(ProcedimentoTerapeuticoRNExceptionCode.MSG_ASSUNTO_LIBERACAO_VERSAO_PROTOCOLO.toString(), nomeProtocolo);
				//Quando a situacao eh liberada, adiciona link de retorno para tela de pesquisa.
				url = StringUtils.EMPTY;
				url = "/procedimentoterapeutico/cadastroapoio/pesquisaProtocoloList.xhtml";
				descricaoAba = "Protocolos";
			}
		}
		//Envia email e adiciona notificação a central de pendencias
		if(StringUtils.isNotEmpty(conteudo)){
			if (StringUtils.isNotEmpty(destinatario)) {
				emailUtil.enviaEmail(remetente, destinatario, null, assunto, conteudo);
			}
			if (servidorResponsavel != null) {
				adicionarPendenciaServidorSemEmail(conteudo, url, descricaoAba, servidorResponsavel);
			}
		}
	}
	
	/**#49279 - Adiciona uma notificação a central de pendências do servidor informado sem envio de email**/
	public void adicionarPendenciaServidorSemEmail(String mensagem, String url, String descricaoAba, RapServidores servidor){
		AghCaixaPostal caixaPostal = new AghCaixaPostal();
		caixaPostal.setMensagem(mensagem);
		caixaPostal.setUrlAcao(url);
		caixaPostal.setTituloAbaAcao(descricaoAba);
		caixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		
		if(caixaPostal.getAcaoObrigatoria() == null){
			caixaPostal.setAcaoObrigatoria(true);
		}
		if(caixaPostal.getFormaIdentificacao() == null){
			caixaPostal.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		}
		
		if(caixaPostal.getSeq() == null){
			caixaPostal.setCriadoEm(new Date());
			caixaPostal.setDthrInicio(new Date());
			this.aghuFacade.persistirAghCaixaPostal(caixaPostal);
		}
		this.flush();
		
		//Armazena a pendência para o servidor
		AghCaixaPostalServidor caixaPostalServidor = new AghCaixaPostalServidor();
		caixaPostalServidor.setId(new AghCaixaPostalServidorId(caixaPostal.getSeq(), servidor));
		caixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N);
		this.aghuFacade.persistirAghCaixaPostalServidor(caixaPostalServidor);
		
	}
	
	
	/**
	 * 41719
	 * @param codigoPaciente
	 */
	public List<RegistroIntercorrenciaVO> obterRegistroIntercorrenciaPorPaciente(Integer codigoPaciente){
		List<RegistroIntercorrenciaVO> lista = mptIntercorrenciaDAO.obterRegistroIntercorrenciaPorPaciente(codigoPaciente);
		for(RegistroIntercorrenciaVO vo : lista){
			vo.setProtocolo(obterProtocolosSessao(vo.getCicloSeq()));
		}
		return lista;
	}
	
	private String obterProtocolosSessao(Integer cicloSeq){
		List<RegistroIntercorrenciaVO> lista = mptProtocoloCicloDAO.obterProtocolosSessao(cicloSeq);
		StringBuilder retorno = new StringBuilder();
		String retornoAux = StringUtils.EMPTY;
		if(lista != null && !lista.isEmpty()){
			for(RegistroIntercorrenciaVO vo : lista){
				if(!StringUtils.isEmpty(vo.getProtocoloCicloDescricao())){
					retorno = new StringBuilder();
					retorno.append(vo.getProtocoloCicloDescricao());
					break;
				}else{
					if(!StringUtils.isEmpty(vo.getProtocoloAssistencialTitulo())){
						retorno.append(vo.getProtocoloAssistencialTitulo()).append(" -");
					}
				}
			}
			if(retorno.toString().endsWith("-")){
				retornoAux = retorno.toString().substring(0, retorno.toString().length() -1);
			}else{
				retornoAux = retorno.toString();
			}
		}
		return retornoAux;
	}
	
	public void gravarIntercorrencia(String descricao, Short tpiSeq, Integer sesSeq) throws ApplicationBusinessException{
		if(descricao.length() > 60){
			throw new ApplicationBusinessException(ProcedimentoTerapeuticoRNExceptionCode.DESCRICAO_GRANDE_DEMAIS, Severity.ERROR); 
		}
		if(StringUtils.isEmpty(descricao)){
			throw new ApplicationBusinessException(ProcedimentoTerapeuticoRNExceptionCode.CAMPO_DESCRICAO_OBRIGATORIO, Severity.ERROR); 
		}
		MptIntercorrencia intercorrencia = new MptIntercorrencia();
		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			LOG.error("Problema ao tentar obter servidor pelo usuário logado.", e);
		}
		this.preGravarIntercorrencia(descricao, tpiSeq, intercorrencia, servidorLogado, sesSeq);
		mptIntercorrenciaDAO.persistir(intercorrencia);
		this.flush();
		MptIntercorrenciaJn intercorrenciaJN = this.preGravarIntercorrenciaJn(intercorrencia, servidorLogado);
		mptIntercorrenciaJnDAO.persistir(intercorrenciaJN);
	}

	private void preGravarIntercorrencia(String descricao, Short tpiSeq,
			MptIntercorrencia intercorrencia, RapServidores servidorLogado, Integer sesSeq) {
		MptTipoIntercorrencia tipoIntercorrencia = mptTipoIntercorrenciaDAO.obterPorChavePrimaria(tpiSeq);
		MptSessao sessao = mptSessaoDAO.obterPorChavePrimaria(sesSeq);
		intercorrencia.setCriadoEm(new Date());
		intercorrencia.setDescricao(descricao);
		intercorrencia.setTipoIntercorrencia(tipoIntercorrencia);
		intercorrencia.setIndSituacao(DominioSituacao.A);
		intercorrencia.setServidor(servidorLogado);
		intercorrencia.setSessao(sessao);
	}
	
//	private void gravarIntercorrenciaJn(MptIntercorrencia intercorrencia, RapServidores servidorLogado){
//		MptIntercorrenciaJn intercorrenciaJN = preGravarIntercorrenciaJn(
//				intercorrencia, servidorLogado);
//		mptIntercorrenciaJnDAO.persistir(intercorrenciaJN);
//	}

	private MptIntercorrenciaJn preGravarIntercorrenciaJn(
			MptIntercorrencia intercorrencia, RapServidores servidorLogado) {
		MptIntercorrenciaJn intercorrenciaJN = new MptIntercorrenciaJn();
		
		if(intercorrencia.getDescricao() != null){
			intercorrenciaJN.setDescricao(intercorrencia.getDescricao());
		}
		intercorrenciaJN.setIndSituacao(intercorrencia.getIndSituacao());
		intercorrenciaJN.setNomeUsuario(servidorLogado.getUsuario());
		intercorrenciaJN.setSerMatricula(servidorLogado.getId().getMatricula());
		intercorrenciaJN.setSerVinculoCodigo(servidorLogado.getId().getVinCodigo());
		intercorrenciaJN.setOperacao(DominioOperacoesJournal.INS);
		intercorrenciaJN.setSeq(intercorrencia.getSeq());
		if(intercorrencia.getSessao() != null){
			intercorrenciaJN.setSesSeq(intercorrencia.getSessao().getSeq());
		}
		if(intercorrencia.getTipoIntercorrencia() != null){
			intercorrenciaJN.setTpiSeq(intercorrencia.getTipoIntercorrencia().getSeq());
		}
		return intercorrenciaJN;
	}
}
