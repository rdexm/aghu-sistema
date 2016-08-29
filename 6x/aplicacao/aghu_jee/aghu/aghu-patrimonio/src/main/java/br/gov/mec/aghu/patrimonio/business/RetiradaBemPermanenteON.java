package br.gov.mec.aghu.patrimonio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaoBemPermanente;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioTipoBemPermanente;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmBemPermanentesJn;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmItemRecebProvisoriosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.patrimonio.business.RetiradaBemPermanenteON.RetiradaBemPermanenteONExceptionCode;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosJnDAO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoRetiradaBemPermanenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras da funcionalidade Registrar Retirada de Bem Permanente para Aceite Técnico.
 */
@Stateless
public class RetiradaBemPermanenteON extends BaseBusiness {

	private static final long serialVersionUID = 3021374249091836526L;

	private static final Log LOG = LogFactory.getLog(RetiradaBemPermanenteON.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	
	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO;

	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject 
	private FccCentroCustosDAO fccCentroCustosDAO;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	
	@Inject
	private PtmBemPermanentesJnDAO ptmBemPermanentesJnDAO;
	
	@Inject
	private PtmItemRecebProvisoriosJnDAO ptmItemRecebProvisoriosJnDAO;
	
	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.core.business.BaseBusiness#getLogger()
	 */
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Enum contendo os códigos de erro possíveis para esta ON.
	 */
	public enum RetiradaBemPermanenteONExceptionCode implements BusinessExceptionCode {
		ITENS_PARA_TECNICOS_DISTINTOS, STATUS_INVALIDO_PARA_RETIRADA, FALHA_DETALHAR_ITENS, RETIRADA_MAIOR_QUE_DISPONIVEL, NR_PATRIMONIO_DUPLICADO,
		CAMPO_OBRIGATORIO_QUANTIDADE_RETIRADA, ITENS_PARA_AREA_TECNICAS_DISTINTAS
	}

	/**
	 * Realiza as validações necessárias para a inicialização da tela de Retirada de Bem Permanente.
	 * 
	 * @param itensRetirada - Lista de itens selecionados na tela de Aceite Técnico
	 * @throws ApplicationBusinessException
	 */
	public void validarInicializacaoRetiradaBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws ApplicationBusinessException {

		Integer tecnicoResponsavel = null;
		Short codigoTecnicoResponsavel = null;

		for (AceiteTecnicoParaSerRealizadoVO item : itensRetirada) {
			// RN09, #43471
			if (tecnicoResponsavel == null) {
				tecnicoResponsavel = item.getTecnicoResponsavel();
				codigoTecnicoResponsavel = item.getCodigoTecnicoResponsavel();
			} else if (!tecnicoResponsavel.equals(item.getTecnicoResponsavel())
					|| !codigoTecnicoResponsavel.equals(item.getCodigoTecnicoResponsavel())) {
				throw new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.ITENS_PARA_TECNICOS_DISTINTOS);
			}

			// ON03, #43471
			if (!Integer.valueOf(DominioStatusAceiteTecnico.EM_AVALIACAO_TECNICA.getCodigo()).equals(item.getStatus())) {
				throw new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.STATUS_INVALIDO_PARA_RETIRADA);
			}

			// Atualizando campo QUANTIDADE_DISP
			if (item.getQuantidadeDisponivel() == null) {
				PtmItemRecebProvisorios itemPtm = getPtmItemRecebProvisoriosDAO().obterPorChavePrimaria(item.getSeqItemPatrimonio());

				item.setQuantidadeDisponivel(item.getQuantidade().longValue());
				itemPtm.setQuantidadeDisp(item.getQuantidadeDisponivel());

				getPtmItemRecebProvisoriosDAO().atualizar(itemPtm);
			}
		}
	}

	/**
	 * Realiza as validações necessárias para a inicialização da grid de detalhamento na tela de Retirada de Bem Permanente.
	 * 
	 * @param itensRetirada - Lista de itens selecionados na tela de Aceite Técnico
	 * @throws ApplicationBusinessException
	 */
	public void validarInicializacaoDetalharRetiradaBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws ApplicationBusinessException {

		for (AceiteTecnicoParaSerRealizadoVO item : itensRetirada) {
			// MS01, #43471
			if (item.getQuantidadeRetirada() == null && item.getQuantidadeDisponivel() == null) {
				throw new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.FALHA_DETALHAR_ITENS);
			}
			if (item.getQuantidadeRetirada() == null || item.getQuantidadeRetirada().equals(0l)) {
				throw new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.CAMPO_OBRIGATORIO_QUANTIDADE_RETIRADA);
			}
			// ON04, #43471
			if (item.getQuantidadeDisponivel() == null || item.getQuantidadeRetirada().compareTo(item.getQuantidadeDisponivel()) > 0) {
				throw new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.RETIRADA_MAIOR_QUE_DISPONIVEL);
			}
		}
	}

	/**
	 * Realiza a retirada de Bens Permanentes.
	 * 
	 * @param itensDetalhados - Itens a serem retirados
	 * @throws ApplicationBusinessException 
	 */
	public void registrarRetiradaBemPermanente(List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhados) throws ApplicationBusinessException {

		// RN10, #43471
		List<Long> numerosUsados = new ArrayList<Long>();
		for (DetalhamentoRetiradaBemPermanenteVO item : itensDetalhados) {
			if (numerosUsados.contains(item.getNumeroBem()) || getPtmBemPermanentesDAO().obterBemPermanentePorNumeroBem(item.getNumeroBem()) != null) {
				throw new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.NR_PATRIMONIO_DUPLICADO, item.getNumeroBem());
			}

			if (item.getNumeroBem() != null) {
				numerosUsados.add(item.getNumeroBem());
			}
		}

		Map<Long, List<DetalhamentoRetiradaBemPermanenteVO>> itensAgrupados = new HashMap<Long, List<DetalhamentoRetiradaBemPermanenteVO>>();

		for (DetalhamentoRetiradaBemPermanenteVO item : itensDetalhados) {
			if (!itensAgrupados.containsKey(item.getAceiteVO().getSeqItemPatrimonio())) {
				itensAgrupados.put(item.getAceiteVO().getSeqItemPatrimonio(), new ArrayList<DetalhamentoRetiradaBemPermanenteVO>());
			}

			itensAgrupados.get(item.getAceiteVO().getSeqItemPatrimonio()).add(item);
		}

		Integer centroCustoPatrimonio = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PATRIMONIO);

		for (Long seqItemReceb : itensAgrupados.keySet()) {
			PtmItemRecebProvisorios item = getPtmItemRecebProvisoriosDAO().obterPorChavePrimaria(seqItemReceb);

			List<PtmBemPermanentes> bens = getPtmBemPermanentesDAO().listarBemPermanentePorItemRecebimento(seqItemReceb);
			
			RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();

			AceiteTecnicoParaSerRealizadoVO aceiteVO = null;
			
			if (bens.isEmpty()) {
				for (DetalhamentoRetiradaBemPermanenteVO itemDet : itensAgrupados.get(seqItemReceb)) {
					if (aceiteVO == null) {
						aceiteVO = itemDet.getAceiteVO();
					}

					PtmBemPermanentes bem = new PtmBemPermanentes();
					
					bem.setNumeroSerie(itemDet.getNumeroSerie());
					bem.setNumeroBem(itemDet.getNumeroBem());
					bem.setPtmItemRecebProvisorios(item);
					bem.setCcSeq(aceiteVO.getCentroCusto());

					if (itemDet.getEsl() == null) {
						bem.setTipo(String.valueOf(DominioTipoBemPermanente.AQUISICAO.getCodigo()));
					} else {
						bem.setTipo(String.valueOf(DominioTipoBemPermanente.OUTRO.getCodigo()));
					}

					bem.setSituacao(String.valueOf(DominioSituacaoBemPermanente.AQUISICAO_TEMPORARIA.getCodigo()));
					bem.setAtaSeq(aceiteVO.getAreaTecAvaliacao());
					bem.setDataCriacao(new Date());
					bem.setDataAlteracao(new Date());
					bem.setServidor(getServidorLogadoFacade().obterServidorLogado());
					bem.setForSeq(null);
					bem.setMatSeq(aceiteVO.getCodigo());
					bem.setVgeSeq(null);
					bem.setGndSeq(null);

					getPtmBemPermanentesDAO().persistir(bem);
				}

				if (aceiteVO.getQuantidade() > aceiteVO.getQuantidadeRetirada()) {
					for (long i = aceiteVO.getQuantidade(); i > aceiteVO.getQuantidadeRetirada(); i--) {
						PtmBemPermanentes bem = new PtmBemPermanentes();
						
						bem.setPtmItemRecebProvisorios(item);
						bem.setCcSeq(centroCustoPatrimonio);

						if (aceiteVO.getEsl() == null) {
							bem.setTipo(String.valueOf(DominioTipoBemPermanente.AQUISICAO.getCodigo()));
						} else {
							bem.setTipo(String.valueOf(DominioTipoBemPermanente.OUTRO.getCodigo()));
						}

						bem.setSituacao(String.valueOf(DominioSituacaoBemPermanente.AQUISICAO_TEMPORARIA.getCodigo()));
						bem.setAtaSeq(aceiteVO.getAreaTecAvaliacao());
						bem.setDataCriacao(new Date());
						bem.setDataAlteracao(new Date());
						bem.setServidor(getServidorLogadoFacade().obterServidorLogado());
						bem.setForSeq(null);
						bem.setMatSeq(aceiteVO.getCodigo());
						bem.setVgeSeq(null);
						bem.setGndSeq(null);

						getPtmBemPermanentesDAO().persistir(bem);
					}
				}
			} else {
				Iterator<PtmBemPermanentes> bemIt = bens.iterator();

				for (DetalhamentoRetiradaBemPermanenteVO itemDet : itensAgrupados.get(seqItemReceb)) {
					if (aceiteVO == null) {
						aceiteVO = itemDet.getAceiteVO();
					}

					PtmBemPermanentes bem = null;
					while (bem == null || !bem.getCcSeq().equals(centroCustoPatrimonio)) {
						bem = bemIt.next();
					}
					
					gravarHistoricoRetiradaBemPermanente(bem, servidor);
					PtmItemRecebProvisorios pirp = this.ptmItemRecebProvisoriosDAO
							.obterPorChavePrimaria(item.getSeq());
					garavrHistoricoItemReceProvisorio(pirp, servidor);
					
					bem.setNumeroSerie(itemDet.getNumeroSerie());
					bem.setNumeroBem(itemDet.getNumeroBem());
					bem.setCcSeq(aceiteVO.getCentroCusto());

					if (itemDet.getEsl() == null) {
						bem.setTipo(String.valueOf(DominioTipoBemPermanente.AQUISICAO.getCodigo()));
					} else {
						bem.setTipo(String.valueOf(DominioTipoBemPermanente.OUTRO.getCodigo()));
					}

					bem.setSituacao(String.valueOf(DominioSituacaoBemPermanente.AQUISICAO_TEMPORARIA.getCodigo()));
					bem.setDataAlteracao(new Date());
					bem.setServidor(getServidorLogadoFacade().obterServidorLogado());

					getPtmBemPermanentesDAO().atualizar(bem);
				}
			}

			// Atualizando campo QUANTIDADE_DISP
			item.setQuantidadeDisp(item.getQuantidadeDisp() - aceiteVO.getQuantidadeRetirada());

			getPtmItemRecebProvisoriosDAO().atualizar(item);
		}
	}

	/**
	 *  #43473 Obtem os itens do subrelatório
	 * 
	 */
	public List<AceiteTecnicoParaSerRealizadoVO> obterSubItensRelatorioProtocoloRetBensPermanentes(List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList, 
			List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta, boolean reimpressao){
		ScoFornecedor fornecedor = null;
			for (AceiteTecnicoParaSerRealizadoVO aceiteVO : itemRetiradaList) {
				fornecedor = scoFornecedorDAO.pesquisarFornecedorRetiradaBemPermaAceiteTec(aceiteVO);
				aceiteVO.setFornecedor(fornecedor.getRazaoSocial());
				if(fornecedor.getCpf() != null && fornecedor.getCpf().longValue() >= 0){
					aceiteVO.setCgc(fornecedor.getCpf());
				}
				if(fornecedor.getCgc() != null && fornecedor.getCgc().longValue() >= 0){
					aceiteVO.setCgc(fornecedor.getCgc());
				}
				String nomeTec = registroColaboradorFacade.buscarNomeResponsavelPorMatricula(aceiteVO.getCodigoTecnicoResponsavel(), aceiteVO.getTecnicoResponsavel());
				aceiteVO.setNomeTecnicoResp(nomeTec);
				FccCentroCustos centro = fccCentroCustosDAO.obterNomeSeqCentroCusto(aceiteVO.getAreaTecAvaliacao());
				aceiteVO.setNomeCentroCusto(centro.getDescricao());
				aceiteVO.setCentroCusto(centro.getCodigo());
				String nomeArea = (ptmAreaTecAvaliacaoDAO.obterPorChavePrimaria(aceiteVO.getAreaTecAvaliacao())).getNomeAreaTecAvaliacao();
				aceiteVO.setNomeArea(nomeArea);
				aceiteVO.setAceiteVO(aceiteVO);
				for (DetalhamentoRetiradaBemPermanenteVO detalheVO : itensDetalhamentoListCompleta) {
					if(aceiteVO.obterRecebItem().equals(detalheVO.getAceiteVO().obterRecebItem()) && !reimpressao){
						aceiteVO.getItens().add(detalheVO);
					}
				}
			}
		return itemRetiradaList;
	}
	
	/**
	 * Realiza as validações necessárias para a inicialização da tela de Reimpressaõ de Protocolo de Bem Permanente.
	 * 
	 * @param itensRetirada - Lista de itens selecionados na tela de Aceite Técnico
	 * @param areaTecnica - Área Tecnica selecionada na tela de Aceite Técnico
	 * @throws ApplicationBusinessException, BaseListException 
	 */
	public void validarInicializacaoReImpressaoBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws  BaseListException{

		BaseListException lista = new BaseListException();
		Integer tecnicoResponsavel = null;
		Short codigoTecnicoResponsavel = null;
		
		Integer tempAreaTecAvaliacao = null;
		
		Boolean itensParaTecnicosDestinos = Boolean.FALSE;
		Boolean itensParaAreasTecnicasDistintas = Boolean.FALSE;
		Boolean controle = Boolean.FALSE;

		for (AceiteTecnicoParaSerRealizadoVO item : itensRetirada) {
			// RN02, #46111
			if (tecnicoResponsavel == null) {
				tecnicoResponsavel = item.getTecnicoResponsavel();
				codigoTecnicoResponsavel = item.getCodigoTecnicoResponsavel();
			} else if (!tecnicoResponsavel.equals(item.getTecnicoResponsavel())
					|| !codigoTecnicoResponsavel.equals(item.getCodigoTecnicoResponsavel())) {
				itensParaTecnicosDestinos = Boolean.TRUE;
				
			} 
			
			if(tempAreaTecAvaliacao == null){
				tempAreaTecAvaliacao = item.getAreaTecAvaliacao();
			} else if(!tempAreaTecAvaliacao.equals(item.getAreaTecAvaliacao())){
				itensParaAreasTecnicasDistintas = Boolean.TRUE;
				
			}
			
		}
		
		if(itensParaTecnicosDestinos && itensParaAreasTecnicasDistintas){
			lista.add(new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.ITENS_PARA_TECNICOS_DISTINTOS));
			lista.add(new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.ITENS_PARA_AREA_TECNICAS_DISTINTAS));
			controle = Boolean.TRUE;
		} else if(itensParaTecnicosDestinos && !controle){
			lista.add(new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.ITENS_PARA_TECNICOS_DISTINTOS));
		} else if(itensParaAreasTecnicasDistintas && !controle){
			lista.add(new ApplicationBusinessException(RetiradaBemPermanenteONExceptionCode.ITENS_PARA_AREA_TECNICAS_DISTINTAS));
		}
		if(lista.hasException()){
			throw lista;
		}
	}
	
	public void gravarHistoricoRetiradaBemPermanente(PtmBemPermanentes pbp, RapServidores servidor) {

		PtmBemPermanentesJn pbpJn = new PtmBemPermanentesJn();

		pbpJn.setCcSeq(pbp.getCcSeq());// nullable
		pbpJn.setAtaSeq(pbp.getAtaSeq());
		pbpJn.setForSeq(pbp.getForSeq());
		pbpJn.setGndSeq(pbp.getGndSeq());
		pbpJn.setMatSeq(pbp.getMatSeq());
		pbpJn.setVgeSeq(pbp.getVgeSeq());
		pbpJn.setSeq(pbp.getSeq().intValue());// nullable
		pbpJn.setIrpSeq(pbp.getIrpSeq().intValue());// nullable
		pbpJn.setNumeroBem(pbp.getNumeroBem());
		pbpJn.setBemPenhora(pbp.getBemPenhora());
		pbpJn.setNumeroSerie(pbp.getNumeroSerie());
		pbpJn.setNumeroProcesso(pbp.getNumeroProcesso());
		pbpJn.setSituacao(pbp.getSituacao());// nullable
		pbpJn.setTipo(pbp.getTipo());// nullable
		pbpJn.setJnData(new Date());// nullable
		pbpJn.setDataAlteracao(new Date());
		pbpJn.setDataCriacao(pbp.getDataCriacao());
		pbpJn.setDataAquisicao(pbp.getDataAquisicao());
		pbpJn.setDetalhamento(pbp.getDetalhamento());
		pbpJn.setProSeq(pbp.getProSeq());
		pbpJn.setJnUsuario(servidor.getUsuario());// nullable
		pbpJn.setSerMatricula(servidor.getId().getMatricula());// nullable
		pbpJn.setSerVinCodigo(servidor.getId().getVinCodigo().intValue());// nullable
		pbpJn.setValorAtual(pbp.getValorAtual());
		pbpJn.setValorInicial(pbp.getValorInicial());
		pbpJn.setJnOperacao(DominioOperacaoBanco.UPD);// nullable

		this.ptmBemPermanentesJnDAO.persistir(pbpJn);
	}
	
	public void garavrHistoricoItemReceProvisorio(PtmItemRecebProvisorios pirp, RapServidores servidor){

		PtmItemRecebProvisoriosJn ptmItemRecebProvisoriosJn = new PtmItemRecebProvisoriosJn();
		
		ptmItemRecebProvisoriosJn.setNomeUsuario(servidor.getUsuario());
		ptmItemRecebProvisoriosJn.setOperacao(DominioOperacoesJournal.UPD);
		if(pirp.getAtaSeq() != null){			
			ptmItemRecebProvisoriosJn.setAtaSeq(pirp.getAtaSeq());
		}
		ptmItemRecebProvisoriosJn.setDataRecebimento(pirp.getDataRecebimento());
		ptmItemRecebProvisoriosJn.setDataUltimaAlteracao(pirp.getDataUltimaAlteracao());
		ptmItemRecebProvisoriosJn.setPagamentoParcial(pirp.getPagamentoParcial());
		ptmItemRecebProvisoriosJn.setSceItemRecebProvisorio(pirp.getSceItemRecebProvisorio());
		ptmItemRecebProvisoriosJn.setSeq(pirp.getSeq());
		ptmItemRecebProvisoriosJn.setServidor(pirp.getServidor());
		if(pirp.getServidorTecPadrao() != null){			
			ptmItemRecebProvisoriosJn.setServidorTecPadrao(pirp.getServidorTecPadrao());
		}
		ptmItemRecebProvisoriosJn.setStatus(pirp.getStatus());
		this.ptmItemRecebProvisoriosJnDAO.persistir(ptmItemRecebProvisoriosJn);
	}
	
	public PtmBemPermanentesDAO getPtmBemPermanentesDAO() {
		return ptmBemPermanentesDAO;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public PtmItemRecebProvisoriosDAO getPtmItemRecebProvisoriosDAO() {
		return ptmItemRecebProvisoriosDAO;
	}

}