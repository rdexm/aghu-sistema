package br.gov.mec.aghu.compras.pac.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoEtapaModPacDAO;
import br.gov.mec.aghu.compras.dao.ScoEtapaPacDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.compras.dao.ScoLogEtapaPacDAO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioApVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.model.ScoEtapaPac;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLogEtapaPac;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class AtualizarSituacaoEtapasPACRN extends BaseBusiness {
	private static final long serialVersionUID = 172727199923425L;
	
	private static final Log LOG = LogFactory.getLog(AtualizarSituacaoEtapasPACRN.class);

	private static final String EM_TRAMITE_DESDE = "Em trâmite desde ";
	private static final String DIAS = " dias";
	private static final String FORMATAR_DATA = "dd/MM/yyyy";
	
	@Inject
	private ScoEtapaPacDAO scoEtapaPacDAO;
	
	@Inject
	private ScoLogEtapaPacDAO scoLogEtapaPacDAO;
	
	@Inject
	private ScoEtapaModPacDAO scoEtapaModPacDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;
	
	
	public void atualizarSituacaoEtapaPAC(EtapasRelacionadasPacVO vo, RapServidores servidor){
		
		ScoEtapaPac scoEtapaPac = getScoEtapaPacDAO().obterPorChavePrimaria(vo.getCodigoEtapa());
		
		this.gerarLogAtualizacaoEtapaPAC(scoEtapaPac);
		
		scoEtapaPac.setApontamentoUsuario(vo.getApontamentoUsuario());
		scoEtapaPac.setSituacao(vo.getSituacao());
		scoEtapaPac.setDataApontamento(new Date());
		scoEtapaPac.setServidor(servidor);
		
		getScoEtapaPacDAO().atualizar(scoEtapaPac);
	}
 	
	private void gerarLogAtualizacaoEtapaPAC(ScoEtapaPac scoEtapaPac) {
		ScoLogEtapaPac log = new ScoLogEtapaPac();
		
		log.setEtapa(scoEtapaPac);
		log.setSituacao(scoEtapaPac.getSituacao());
		log.setTempoPrevisto(scoEtapaPac.getTempoPrevisto());
		log.setApontamentoUsuario(scoEtapaPac.getApontamentoUsuario());
		log.setServidor(scoEtapaPac.getServidor());
		log.setDataApontamento(scoEtapaPac.getDataApontamento());
		log.setDataAlteracao(new Date());
	
		getScoLogEtapaPacDAO().persistir(log);
	}

	public EtapasRelacionadasPacVO verificaNecessidadeSalvarEtapaPAC(EtapasRelacionadasPacVO etapaPac, Integer numeroLicitacao){
		if(!verificarPACAcompanhadoIndividualmente(numeroLicitacao)){
			etapaPac.setCodigoEtapa(this.gravarEtapaPAC(etapaPac,  numeroLicitacao));
			return etapaPac;
		}
		return etapaPac;
	}
	
	private Integer gravarEtapaPAC(EtapasRelacionadasPacVO etapaPac, Integer numeroLicitacao) {
		
		ScoEtapaModPac scoEtapaModPac = getScoEtapaModPacDAO().obterPorChavePrimaria(etapaPac.getCodigoEtapa());
		ScoEtapaPac scoEtapaPac = new ScoEtapaPac();
		
		scoEtapaPac.setLicitacao(getScoLicitacaoDAO().obterPorChavePrimaria(numeroLicitacao));
		scoEtapaPac.setLocalizacaoProcesso(scoEtapaModPac.getLocalizacaoProcesso());
		scoEtapaPac.setDescricaoEtapa(scoEtapaModPac.getDescricao());
		scoEtapaPac.setSituacao(DominioSituacaoEtapaPac.PD);
		scoEtapaPac.setTempoPrevisto(scoEtapaModPac.getNumeroDias());
		scoEtapaPac.setApontamentoUsuario(null);
		scoEtapaPac.setServidor(null);
		scoEtapaPac.setDataApontamento(null);

		getScoEtapaPacDAO().persistir(scoEtapaPac);
		
		return scoEtapaPac.getCodigo();
		
		
	}

	public List<EtapasRelacionadasPacVO> listarEtapasRelacionadasPAC(Integer numeroLicitacao, Short codigoLocalizacao, String codigoModalidade){
		List<EtapasRelacionadasPacVO> listaEtapasRelacionadasPAC = new ArrayList<EtapasRelacionadasPacVO>();
		
		if(verificarPACAcompanhadoIndividualmente(numeroLicitacao)){
			listaEtapasRelacionadasPAC = getScoEtapaPacDAO().listarEtapasRelacionadasPACPorLicitacaoLocalizacao(numeroLicitacao, codigoLocalizacao);
		}else{
			List<DominioObjetoDoPac> listaObjetoPAC = this.verificarModalidadeObjetoPAC(numeroLicitacao);
			listaEtapasRelacionadasPAC = getScoEtapaModPacDAO().listarEtapasRelacionadasPACPorLicitacaoLocalizacao(codigoModalidade, codigoLocalizacao, listaObjetoPAC);
			this.alterarRegistrosParaSituacaoPendente(listaEtapasRelacionadasPAC);
		}
		return listaEtapasRelacionadasPAC;
	}
	
	private void alterarRegistrosParaSituacaoPendente(List<EtapasRelacionadasPacVO> listaEtapasRelacionadasPAC) {
		for (EtapasRelacionadasPacVO etapasRelacionadasPacVO : listaEtapasRelacionadasPAC) {
			etapasRelacionadasPacVO.setSituacao(DominioSituacaoEtapaPac.PD);
		}
	}

	public Long pesquisarLocalPACPorNumeroDescricaoCount(Object param, Integer numeroLicitacao, String codigoModalidade){
		
		if(verificarPACAcompanhadoIndividualmente(numeroLicitacao)){
			return getScoEtapaPacDAO().pesquisarLocaisPrevistosPACCount(param, numeroLicitacao);
		} else {
			List<DominioObjetoDoPac> listaObjetoPAC = this.verificarModalidadeObjetoPAC(numeroLicitacao);
			return getScoEtapaModPacDAO().listarEtapaModPacCount(param, codigoModalidade, listaObjetoPAC);
		}
	}
	
	public List<LocalPACVO> pesquisarLocalPACPorNumeroDescricao(Object param, Integer numeroLicitacao, String codigoModalidade){

		if(verificarPACAcompanhadoIndividualmente(numeroLicitacao)){
			return getScoEtapaPacDAO().pesquisarLocaisPrevistosPAC(param, numeroLicitacao);
		} else {
			List<DominioObjetoDoPac> listaObjetoPAC = this.verificarModalidadeObjetoPAC(numeroLicitacao);
			return getScoEtapaModPacDAO().listarEtapaModPac(param, codigoModalidade, listaObjetoPAC);
		}
	}
	
	//RN03 #38522
	private Boolean verificarPACAcompanhadoIndividualmente(Integer numeroLicitacao){
		List<ScoEtapaPac> listaEtapasPac = getScoEtapaPacDAO().obterEtapaPacPorLicitacao(numeroLicitacao);
		
		return listaEtapasPac != null && !listaEtapasPac.isEmpty();
	}
	
	public String alterarTempoTotal(String dataInicioFim){
		if(dataInicioFim != null && dataInicioFim.contains(EM_TRAMITE_DESDE)){
			String dateInicial = dataInicioFim.substring(EM_TRAMITE_DESDE.length(), dataInicioFim.length());
		    return DateUtil.obterQtdDiasEntreDuasDatas(this.parseDate(dateInicial), new Date()).toString().concat(DIAS);
		}
		else if (dataInicioFim != null){
			String dataInicial = dataInicioFim.substring(0, FORMATAR_DATA.length());
			String dataFinal = dataInicioFim.substring(FORMATAR_DATA.length() + 2, dataInicioFim.length());
			return DateUtil.obterQtdDiasEntreDuasDatas(this.parseDate(dataInicial), this.parseDate(dataFinal)).toString().concat(DIAS);
		}
		
		return "";
	}
	
	private Date parseDate(String data){
		DateFormat formatter = new SimpleDateFormat(FORMATAR_DATA);  
		try {
			return (Date) formatter.parse(data);
		} catch (ParseException e) {
			LOG.error("Erro ao formatar data.", e);
		}
		return null;
	}

	private List<DominioObjetoDoPac> verificarModalidadeObjetoPAC(Integer numeroLicitacao) {
		List<ScoItemLicitacao> listaItensLicitacao = getItemLicitacaoDAO().listarModalidadeItensLicitacaoPorNumeroLicitacao(numeroLicitacao);
		
		List<DominioObjetoDoPac> listaTipoSilicitacaoCompra = new ArrayList<DominioObjetoDoPac>();
		for (ScoItemLicitacao scoItemLicitacao : listaItensLicitacao) {
			for (ScoFaseSolicitacao scoFaseSolicitacao : scoItemLicitacao.getFasesSolicitacao()) {
				if ((scoFaseSolicitacao.getSolicitacaoDeCompra() != null && scoFaseSolicitacao.getSolicitacaoDeCompra().getNumero() != 0) &&
						(scoFaseSolicitacao.getSolicitacaoServico() != null && scoFaseSolicitacao.getSolicitacaoServico().getNumero() != 0)){
					listaTipoSilicitacaoCompra.add(DominioObjetoDoPac.M);
					listaTipoSilicitacaoCompra.add(DominioObjetoDoPac.S);
					break;
				}
				
				else if(scoFaseSolicitacao.getSolicitacaoDeCompra() != null && scoFaseSolicitacao.getSolicitacaoDeCompra().getNumero() != 0){
					listaTipoSilicitacaoCompra.add(DominioObjetoDoPac.M);
					break;
				}
				
				else if(scoFaseSolicitacao.getSolicitacaoServico() != null && scoFaseSolicitacao.getSolicitacaoServico().getNumero() != 0){
					listaTipoSilicitacaoCompra.add(DominioObjetoDoPac.S);
					break;
				}
				break;
			}
		}
		return listaTipoSilicitacaoCompra;
	}


	public String obterLocalidadeAtualPacPorNumLicitacao(Integer numeroLicitacao){
		Object objLocalidade = this.getScoAndamentoProcessoCompraDAO().obterLocalidadeAtual(numeroLicitacao);
		
		if(objLocalidade != null){
			return objLocalidade.toString();
		} else {
			return null;
		}
	}
	
	public String obterTempoTotal(Integer numeroLicitacao){
		List<RelatorioApVO> listaDatas = obterListaDataEntradaSaida(numeroLicitacao);
		
		for (RelatorioApVO relatorioApVO : listaDatas) {
			return this.formatarDataTempoTotal(relatorioApVO);
		}
		return null;
	}
	
	private String formatarDataTempoTotal(RelatorioApVO relatorioApVO) {
		Integer tempo = null;
		if(relatorioApVO.getDtEntrada() != null && relatorioApVO.getDtSaida() != null){
			tempo = DateUtil.obterQtdDiasEntreDuasDatas(relatorioApVO.getDtEntrada(), relatorioApVO.getDtSaida());			
		}
		else if (relatorioApVO.getDtEntrada() != null && relatorioApVO.getDtSaida() == null){
			tempo = DateUtil.obterQtdDiasEntreDuasDatas(relatorioApVO.getDtEntrada(), new Date());	
		}
		
		if(tempo != null){
			return tempo.toString().concat(DIAS);
		}
		else {
			return null;
		}
		
	}
	public List<String> listarHistoricoEtapas(Integer numeroLicitacao){
		
		List<RelatorioApVO> listaDatas = obterListaDataEntradaSaida(numeroLicitacao);
		
		List<String> listaRetornoDatas = new ArrayList<String>();
		for (RelatorioApVO relatorioApVO : listaDatas) {
			this.formatarDataHistoricoLocal(relatorioApVO, listaRetornoDatas);
		}

		return listaRetornoDatas;
	}

	public List<RelatorioApVO> obterListaDataEntradaSaida(Integer numeroLicitacao) {
		Short lcpCodigo = null;
		Object objCodigoLocalidade = getScoAndamentoProcessoCompraDAO().obterCodigoLocalidadeAtual(numeroLicitacao);
		if(objCodigoLocalidade != null){
			lcpCodigo = Short.valueOf(objCodigoLocalidade.toString());
		}
		List<RelatorioApVO> listaDatas = getScoAndamentoProcessoCompraDAO().listarEntradaSaidaAndamentoProcessoCompras(numeroLicitacao, lcpCodigo);
		return listaDatas;
	}
	
	private void formatarDataHistoricoLocal(RelatorioApVO relatorioApVO, List<String> listaRetornoDatas) {
		String data = null;
		if(relatorioApVO.getDtEntrada() != null && relatorioApVO.getDtSaida() != null){
			data = formataData(relatorioApVO.getDtEntrada()).concat(" a ").concat(formataData(relatorioApVO.getDtSaida()));
			listaRetornoDatas.add(data);
		}
		else if (relatorioApVO.getDtEntrada() != null && relatorioApVO.getDtSaida() == null){
			data = EM_TRAMITE_DESDE.concat(formataData(relatorioApVO.getDtEntrada()));
			listaRetornoDatas.add(data);
		}
	}

	private String formataData(Date original){
		return DateUtil.dataToString(original, FORMATAR_DATA);
	}
	
	public Integer obterCodigoLocalidadeAtualPacPorNumLicitacao(Integer numeroLicitacao){
		Object objCodigoLocalidade = this.getScoAndamentoProcessoCompraDAO().obterCodigoLocalidadeAtual(numeroLicitacao);
		
		if(objCodigoLocalidade != null){
			return Integer.valueOf(objCodigoLocalidade.toString());
		} else {
			return null;
		}
	}
	
	ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO(){
		return scoAndamentoProcessoCompraDAO;
	}
	
	ScoEtapaPacDAO getScoEtapaPacDAO(){
		return scoEtapaPacDAO;
	}
	
	ScoItemLicitacaoDAO getItemLicitacaoDAO(){
		return scoItemLicitacaoDAO;
	}
		

	ScoLicitacaoDAO getScoLicitacaoDAO(){
		return scoLicitacaoDAO;
	}
	
	ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO(){
		return scoLocalizacaoProcessoDAO;
	}
	
	ScoEtapaModPacDAO getScoEtapaModPacDAO(){
		return scoEtapaModPacDAO;
	}
	
	ScoLogEtapaPacDAO getScoLogEtapaPacDAO(){
		return scoLogEtapaPacDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
