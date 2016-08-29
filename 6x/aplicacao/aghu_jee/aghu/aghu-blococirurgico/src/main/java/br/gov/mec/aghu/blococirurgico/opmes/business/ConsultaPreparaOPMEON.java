package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.ConsultaPreparaOPMEFiltroVO;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitacaoCompraMaterialVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConsultaPreparaOPMEON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaPreparaOPMEON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;

	@EJB
	private ConsultaPreparaOPMERN consultaPreparaOPMERN;

	@EJB
	private IComprasCadastrosBasicosFacade iComprasCadastrosBasicosFacade;

	@EJB
	private IComprasFacade iComprasFacade;
	
	@EJB
	private ISolicitacaoComprasFacade iSolicitacaoComprasFacade;

	@EJB
	private ICentroCustoFacade iCentroCustoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -3158614808272742394L;
	private final static Short PONTO_DE_PARADA_ATUAL = 1;
	private final static Short PROXIMO_PONTO_DE_PARADA = 2;
	
	public enum ConsultaPreparaOPMEONExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_GERACAO_SC_REQ
	}
	
	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPME(Short requisicaoSelecionada) throws ApplicationBusinessException {
		
		InfoProcdCirgRequisicaoOPMEVO vo = getMbcRequisicaoOpmesDAO().consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPME(requisicaoSelecionada);
		getConsultaPreparaOPMERN().processarInfoProcdCirgRequisicao(vo);
		return vo;
	}
	
	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(Short requisicaoSelecionada) throws ApplicationBusinessException {
		
		InfoProcdCirgRequisicaoOPMEVO vo = getMbcRequisicaoOpmesDAO().consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(requisicaoSelecionada);
		getConsultaPreparaOPMERN().processarInfoProcdCirgRequisicao(vo);
		return vo;
	}

	public List<MateriaisProcedimentoOPMEVO> consultarMaterialProcedimentoOPME(ConsultaPreparaOPMEFiltroVO vo) throws BaseException {

		getConsultaPreparaOPMERN().validarFiltro(vo);
		List<MateriaisProcedimentoOPMEVO> list = getMbcMateriaisItemOpmesDAO().obterMateriais(vo.getRequisicaoSelecionada(), vo.getLicitado());
		for (MateriaisProcedimentoOPMEVO materiaisProcedimentoOPMEVO : list) {
			materiaisProcedimentoOPMEVO.setRequerido(DominioRequeridoItemRequisicao.valueOf(materiaisProcedimentoOPMEVO.getRequeridoString()));
		}
		List<MateriaisProcedimentoOPMEVO> retorno =  getConsultaPreparaOPMERN().processarListaMateriaisDoProcedimento(list);
		
		return retorno;
	}
	
	public ScoSolicitacaoDeCompra persistirSolicitacaoCompraMaterial(SolicitacaoCompraMaterialVO vo) throws BaseException {

			ScoSolicitacaoDeCompra solCompra = popularScoSolicitacaoDeCompra(vo);
			iSolicitacaoComprasFacade.inserirScoSolicitacaoDeCompra(solCompra);
			MbcMateriaisItemOpmes mbcMateriaisItemOpmes = mbcMateriaisItemOpmesDAO.obterPorChavePrimaria(vo.getMatProcOPME().getMaterialItemSeq());
			mbcMateriaisItemOpmes.setSlcNumero(solCompra.getNumero());
			mbcMateriaisItemOpmesDAO.atualizar(mbcMateriaisItemOpmes);
			return solCompra;
			
	}

	private ScoSolicitacaoDeCompra popularScoSolicitacaoDeCompra(SolicitacaoCompraMaterialVO vo) {
		ScoSolicitacaoDeCompra scoSolicitacaoDeCompra = new ScoSolicitacaoDeCompra();
		
		scoSolicitacaoDeCompra.setPontoParada(getScoPontoParadaSolicitacao(PONTO_DE_PARADA_ATUAL));
		scoSolicitacaoDeCompra.setPontoParadaProxima(getScoPontoParadaSolicitacao(PROXIMO_PONTO_DE_PARADA));
		if(vo != null && vo.getMatProcOPME() != null && vo.getMatProcOPME().getMaterialCodigo() != null){
			scoSolicitacaoDeCompra.setMaterial(getScoMaterial(vo.getMatProcOPME().getMaterialCodigo()));
		}else {
			Integer maxId = getComprasFacade().obterScoMaterialMaxId();
			scoSolicitacaoDeCompra.setMaterial(this.getComprasFacade().obterScoMaterial(maxId));
		}
		scoSolicitacaoDeCompra.setCentroCusto(vo.getServidorLogado().getCentroCustoLotacao());
		scoSolicitacaoDeCompra.setCentroCustoAplicada(getFccCentroCustos(vo.getInfoReqOPME().getEspCentroCusto()));
		scoSolicitacaoDeCompra.setDtSolicitacao(new Date());
		scoSolicitacaoDeCompra.setDtDigitacao(new Date());
		scoSolicitacaoDeCompra.setQtdeSolicitada(vo.getMatProcOPME().getQuantidadeAutorizadaHospital().longValue());
		scoSolicitacaoDeCompra.setExclusao(Boolean.FALSE);
		scoSolicitacaoDeCompra.setUrgente(Boolean.TRUE);
		scoSolicitacaoDeCompra.setDevolucao(Boolean.FALSE);
		scoSolicitacaoDeCompra.setGeracaoAutomatica(Boolean.TRUE);
		scoSolicitacaoDeCompra.setEfetivada(Boolean.FALSE);
		scoSolicitacaoDeCompra.setFundoFixo(Boolean.FALSE);
		if(vo != null && vo.getMatProcOPME() != null && vo.getMatProcOPME().getUnidadeMedidaCodigo() != null){
			scoSolicitacaoDeCompra.setUnidadeMedida(getUnidadeMedida(vo.getMatProcOPME().getUnidadeMedidaCodigo()));
		} else {
			scoSolicitacaoDeCompra.setUnidadeMedida(this.getComprasFacade().obterPorCodigo("M3"));
		}
		scoSolicitacaoDeCompra.setRecebimento(Boolean.FALSE);
		scoSolicitacaoDeCompra.setDiasDuracao((short)1);
		scoSolicitacaoDeCompra.setAplicacao(getAplicacaao(vo));
		scoSolicitacaoDeCompra.setJustificativaUso(vo.getInfoReqOPME().getJustificativaRequisicaoOpme());
		scoSolicitacaoDeCompra.setDescricao(vo.getInfoReqOPME().getObservacoesGerais());
		String motivoUrgencia = validarTamanhoMotivoUrgencia(vo);
		scoSolicitacaoDeCompra.setMotivoUrgencia(motivoUrgencia);
		scoSolicitacaoDeCompra.setValorUnitPrevisto(vo.getMatProcOPME().getValorUnitarioIph());
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		scoSolicitacaoDeCompra.setServidor(servidorLogado);
		
		return scoSolicitacaoDeCompra;
	}

	private String validarTamanhoMotivoUrgencia(SolicitacaoCompraMaterialVO vo) {

		/* INCIDENTE #52218
		 * TO DO - Conforme Informado com a S.Gralha via Telefone com A Sgralha,
		 * o campo motivoUrgência deverá ser aumentado para suportar a
		 * informação completa de "getJustificativaRequisicaoOpme"
		 * preventivamente, neste caso quando os caracteres excederem o tamanho
		 * de 240. Deverá ser aumentado o tamanho do campo caso deseja-se
		 * utilizar a mesma informação
		 */

		if (vo.getInfoReqOPME().getJustificativaRequisicaoOpme().length() > 240) {

			StringBuilder motivoUrgencia = new StringBuilder();
			motivoUrgencia.append(vo.getInfoReqOPME()
					.getJustificativaRequisicaoOpme().toString());
			motivoUrgencia.setLength(240);
			return (motivoUrgencia.toString());

		} else {

			return (vo.getInfoReqOPME().getJustificativaRequisicaoOpme()
					.toString());

		}
	}
	
	
	private String getAplicacaao(SolicitacaoCompraMaterialVO vo) {
		StringBuilder builder =  new StringBuilder(52);
	
		builder
			.append("Paciente: ").append(vo.getInfoReqOPME().getNomePaciente())
			.append("Prontuário: ").append(vo.getInfoReqOPME().getProntuario())
			.append("Data Cirurgia: ").append(vo.getInfoReqOPME().getDataProcedimento())
			.append("Convênio: ")
				.append(vo.getInfoReqOPME().getPlanoConvenio().toString()).append(' ')
				.append(vo.getInfoReqOPME().getConvenioDescricao()).append(" - ")
				.append(vo.getInfoReqOPME().getConvenioSaudePlanoSeq().toString()).append(' ')
				.append(vo.getInfoReqOPME().getFatConvSaudePlanoDesc());
		
		return builder.toString();
	}

	private FccCentroCustos getFccCentroCustos(Integer espCentroCusto) {
		return getCentroCustoFacade().obterCentroCustoPorChavePrimaria(espCentroCusto);
	}

	private ScoUnidadeMedida getUnidadeMedida(String unidadeMedidaCodigo) {
		return getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(unidadeMedidaCodigo);
	}

	private ScoMaterial getScoMaterial(Integer codigo) {
		return getComprasFacade().obterScoMaterialPorChavePrimaria(codigo);
	}

	private ScoPontoParadaSolicitacao getScoPontoParadaSolicitacao(Short codigo) {	
		return getComprasCadastrosBasicosFacade().obterScoPontoParadaSolicitacaoPorChavePrimaria(codigo);
	}
	
	private ICentroCustoFacade getCentroCustoFacade() {
		return this.iCentroCustoFacade;
	}
	
	private IComprasFacade getComprasFacade() {
		return this.iComprasFacade;
	}
	
	private IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return this.iComprasCadastrosBasicosFacade;
	}
	
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO() {
		return mbcRequisicaoOpmesDAO;
	}

	protected ConsultaPreparaOPMERN getConsultaPreparaOPMERN() {
		return consultaPreparaOPMERN;
	}
	
	protected MbcMateriaisItemOpmesDAO getMbcMateriaisItemOpmesDAO() {
		return mbcMateriaisItemOpmesDAO;
	}
}
