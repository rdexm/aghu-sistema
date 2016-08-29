package br.gov.mec.aghu.compras.pac.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.compras.vo.DadosGestorVO;
import br.gov.mec.aghu.compras.vo.DadosPrimeiraAFVO;
import br.gov.mec.aghu.compras.vo.EstatisticaPacVO;
import br.gov.mec.aghu.compras.vo.PACsPendetesVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultarAndamentoProcessoCompraRN extends BaseBusiness {

	private static final long serialVersionUID = -5875255349708260627L;
	
	private static final Log LOG = LogFactory.getLog(ConsultarAndamentoProcessoCompraRN.class);
	
	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;
	
	@Inject
	private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;
	
  
	

	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String PREFIXO="AGDA_";	
	private static final String EXTENSAO=".csv";
	
	private void complementaVORetorno(List<ConsultarAndamentoProcessoCompraDataVO> retornoList, ConsultarAndamentoProcessoCompraVO filtro) {
		for (ConsultarAndamentoProcessoCompraDataVO retorno : retornoList) {
			avaliaNumeroPac(retorno);
			buscaNumeroAFsPorPac(filtro, retorno);
			
			// c5
			obterDadosGestor(retorno);
			
			// c6
			obterDadosLocalizacaoProcesso(retorno);
			
			// c7
			obterResponsavelPeloProcesso(retorno);
			
			retorno.setDiasPermanencia(calculaDiasPermanencia(retorno));
			buildTooltipObjeto(retorno);
		}
		
	}
	
	private void buildTooltipObjeto(ConsultarAndamentoProcessoCompraDataVO retorno) {
		StringBuffer tooltip = new StringBuffer(150);
		tooltip.append("Objeto: ").append(retorno.getObjeto()).append(' ').append(retorno.getGestorValue())
		.append(" \nLocalização: ").append(retorno.getLcpCodigo()).append(" - ").append(retorno.getLocalizaoDescricao())
		.append(" \nRamal: ").append(retorno.getRamal().getNumeroRamal())
		.append(" \nResponsável Local: ").append(retorno.getResponsavelLocal())
		.append(" \nEntrada: ").append(DateUtil.obterDataFormatada(retorno.getDtEntrada(),DateConstants.DATE_PATTERN_DDMMYYYY))
		.append(" \nDias Permanência: ")
		.append(retorno.getDiasPermanencia().toString());
		retorno.setTooltipObjetoValue(tooltip.toString());
	}

	private Integer calculaDiasPermanencia(ConsultarAndamentoProcessoCompraDataVO retorno) {
		Integer diasPermanencia = null;
		if(retorno.getDtSaida() == null){
			diasPermanencia = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(retorno.getDtEntrada(), new Date());
		}else{
			diasPermanencia = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(retorno.getDtEntrada(), retorno.getDtSaida());
		}
		if(diasPermanencia > 1){
			return diasPermanencia;
		}
		return diasPermanencia;
	}

	/**
	 * C1
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompra(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, ConsultarAndamentoProcessoCompraVO filtro) {
		List<ConsultarAndamentoProcessoCompraDataVO> retornoList = getScoAndamentoProcessoCompraDAO().consultarAndamentoProcessoCompra(firstResult, maxResults, orderProperty, asc, filtro);
		if(!retornoList.isEmpty()){
			complementaVORetorno(retornoList, filtro);
		}
		return retornoList;
	}

	/**
	 * C2
	 * @param vo
	 */
	private void avaliaNumeroPac(ConsultarAndamentoProcessoCompraDataVO retorno) {
		ScoLicitacao licitacao = new ScoLicitacao();
		if(retorno.getNumeroPac() != null){
			licitacao = getScoLicitacaoDAO().buscaLicitacoesPorNumero(retorno.getNumeroPac());
			retorno.setModalidade(licitacao.getModalidadeLicitacao());
			retorno.setTipo(licitacao.getTipo());
			retorno.setGeracao(licitacao.getDtDigitacao());
			retorno.setAbertura(licitacao.getDthrAberturaProposta());
			retorno.setObjeto(licitacao.getDescricao());
		}
	}
	
	/**
	 * C3
	 * @param filtro
	 * @param vo
	 */
	private void buscaNumeroAFsPorPac(ConsultarAndamentoProcessoCompraVO filtro, ConsultarAndamentoProcessoCompraDataVO retorno) {
		Integer maisAFs = getScoAutorizacaoFornDAO().buscaNumeroAFsPorPac(filtro, retorno);
		retorno.setMaisAFs(maisAFs);
		if(maisAFs > 0){
			retorno.setSituacao("Em AF");
			obterDadosPrimeiraAF(filtro, retorno);
		}else{
			avaliaPacEncerradoParaSituacao(filtro, retorno);
		}
	}
	
	/**
	 * C4
	 * @param filtro
	 * @param retorno
	 */
	private void obterDadosPrimeiraAF(ConsultarAndamentoProcessoCompraVO filtro, ConsultarAndamentoProcessoCompraDataVO retorno) {
		DadosPrimeiraAFVO dados = getScoAutorizacaoFornDAO().obterDadosPrimeiraAF(filtro, retorno);
		if(dados != null){
			retorno.setAf(dados.getAf());
			retorno.setCp(dados.getCp());
			retorno.setVencimentoContrato(dados.getVencimentoContrato());
			retorno.setSituacaoAF(dados.getSituacao());
			retorno.setDataGeracaoAF(dados.getDataGeracao());
		}
	}
	
	/**
	 * C5
	 * @param retorno
	 */
	private void obterDadosGestor(ConsultarAndamentoProcessoCompraDataVO retorno) {
		DadosGestorVO dadosGestor = getScoLicitacaoDAO().obterDadosGestor(retorno.getNumeroPac());
		if(dadosGestor != null){
			avaliaDadosGestor(retorno, dadosGestor);
		}
	}

	private void avaliaDadosGestor(	ConsultarAndamentoProcessoCompraDataVO retorno,	DadosGestorVO dadosGestor) {
		if(CoreUtil.in(dadosGestor.getMlcCodigo(), "CV", "TP", "CC") && dadosGestor.getMatriculaGestor() == null){
			retorno.setGestorValue("Comissão Licitações");
		}else{
			avaliaMatriculaGestor(retorno, dadosGestor);
		}
	}

	private void avaliaMatriculaGestor(ConsultarAndamentoProcessoCompraDataVO retorno, DadosGestorVO dadosGestor) {
		if(dadosGestor.getMatriculaGestor() != null){
			retorno.setGestorValue("Gestor : "+dadosGestor.getNomeGestor());
		}else{
			retorno.setGestorValue("Gerador PAC : "+dadosGestor.getNomeGestor());
		}
	}
	
	/**
	 * C6
	 * @param retorno 
	 */
	private void obterDadosLocalizacaoProcesso(ConsultarAndamentoProcessoCompraDataVO retorno) {
		ScoLocalizacaoProcesso localizacaoProcesso = getScoLocalizacaoProcessoDAO().obterDadosLocalizacaoProcesso(retorno.getLcpCodigo());
		retorno.setRamal(localizacaoProcesso.getRamal());
		retorno.setLocalizaoDescricao(localizacaoProcesso.getDescricao());
	}
	
	/**
	 * C7
	 * @param retorno
	 */
	private void obterResponsavelPeloProcesso(ConsultarAndamentoProcessoCompraDataVO retorno) {
		retorno.setResponsavelLocal(getScoAndamentoProcessoCompraDAO().obterResponsavelPeloProcesso(retorno.getPacSeq()));
	}
	
	/**
	 * Query para buscar os dados para montar CSV
	 * @param filtro
	 * @return
	 */
	public List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompraForCSV(ConsultarAndamentoProcessoCompraVO filtro) {
		List<ConsultarAndamentoProcessoCompraDataVO> retornoList = getScoAndamentoProcessoCompraDAO().consultarAndamentoProcessoCompraForCSV(filtro);
		if(!retornoList.isEmpty()){
			complementaVORetorno(retornoList, filtro);
		}
		return retornoList;
	}
	
	private void avaliaPacEncerradoParaSituacao(ConsultarAndamentoProcessoCompraVO filtro, ConsultarAndamentoProcessoCompraDataVO retorno) {
		if(DominioSimNao.S.equals(filtro.getPacEncerrado())){
			retorno.setSituacao("Sem AF Pend");
		}else{
			retorno.setSituacao("Sem AF");
		}
	}
	
	public String consultarAndamentoProcessoCompra(List<ConsultarAndamentoProcessoCompraDataVO> dados) throws IOException {
		File file = File.createTempFile(PREFIXO, EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(getCabecalhoAndamentoProcessoCompra());
		for (ConsultarAndamentoProcessoCompraDataVO data : dados){
			out.write(System.getProperty("line.separator"));			
			out.write(getLinhaConsultaAndamentoProcessoCompra(data));
		}
		out.flush();
		out.close();
		return file.getAbsolutePath();
	}

	private String getCabecalhoAndamentoProcessoCompra() {
		StringBuffer header = new StringBuffer(200);
		header.append("PAC").append(SEPARADOR)
		.append("Modl").append(SEPARADOR)
		.append("Tipo").append(SEPARADOR)
		.append("Dt Geração").append(SEPARADOR)
		.append("Dt Abertura").append(SEPARADOR)
		.append("Tempo PAC").append(SEPARADOR)
		.append("Situação").append(SEPARADOR)
		.append("Objeto").append(SEPARADOR)
		.append("Gestor").append(SEPARADOR)
		.append("Nro Local").append(SEPARADOR)
		.append("Localização").append(SEPARADOR)
		.append("Dt Entrada").append(SEPARADOR)
		.append("Dt Saída").append(SEPARADOR)
		.append("Dias Perm").append(SEPARADOR)
		.append("AF").append(SEPARADOR)
		.append("CP").append(SEPARADOR)
		.append("Situação").append(SEPARADOR)
		.append("Dt Geração").append(SEPARADOR)
		.append("Mais AFs").append(SEPARADOR);
		return header.toString();
	}
	
	private String getLinhaConsultaAndamentoProcessoCompra(ConsultarAndamentoProcessoCompraDataVO dados) {
		StringBuffer line = new StringBuffer(200);
		line.append(dados.getNumeroPac()).append(SEPARADOR)
		.append(dados.getModalidade().getCodigo()).append(SEPARADOR)
		.append(dados.getTipo()).append(SEPARADOR)
		.append(DateUtil.dataToString(dados.getGeracao(), DateConstants.DATE_PATTERN_DDMMYYYY)).append(SEPARADOR)
		.append(DateUtil.dataToString(dados.getAbertura(), DateConstants.DATE_PATTERN_DDMMYYYY)).append(SEPARADOR)
		.append(getTempoPACValue(dados))
		.append(dados.getSituacao()).append(SEPARADOR)
		.append(dados.getObjeto()).append(SEPARADOR)
		.append(dados.getGestorValue()).append(SEPARADOR)
		.append(dados.getLcpCodigo()).append(SEPARADOR)
		.append(dados.getLocalizaoDescricao()).append(SEPARADOR)
		.append(DateUtil.dataToString(dados.getDtEntrada(), DateConstants.DATE_PATTERN_DDMMYYYY)).append(SEPARADOR)
		.append(DateUtil.dataToString(dados.getDtSaida(), DateConstants.DATE_PATTERN_DDMMYYYY)).append(SEPARADOR)
		.append(dados.getDiasPermanencia()).append(SEPARADOR)
		.append(getAfValue(dados))
		.append(getCpValue(dados))
		.append(getSituacaoAfValue(dados))
		.append(getDataGeracaoAfValue(dados))
		.append(dados.getMaisAFs()).append(SEPARADOR);
		return line.toString();
	}

	private String getDataGeracaoAfValue(ConsultarAndamentoProcessoCompraDataVO dados) {
		return dados.getDataGeracaoAF() != null ? dados.getDataGeracaoAF()+SEPARADOR : " "+SEPARADOR;
	}

	private String getSituacaoAfValue(ConsultarAndamentoProcessoCompraDataVO dados) {
		return dados.getSituacaoAF() != null ? dados.getSituacaoAF()+SEPARADOR : " "+SEPARADOR;
	}

	private String getCpValue(ConsultarAndamentoProcessoCompraDataVO dados) {
		return dados.getCp() != null ? dados.getCp()+SEPARADOR : " "+SEPARADOR;
	}

	private String getAfValue(ConsultarAndamentoProcessoCompraDataVO dados) {
		return dados.getAf() != null ? dados.getAf()+SEPARADOR : " "+SEPARADOR;
	}

	private Object getTempoPACValue(ConsultarAndamentoProcessoCompraDataVO dados) {
		return dados.getAbertura() == null ? SEPARADOR : calculaTempoPAC(dados.getAbertura())+SEPARADOR;
	}

	private Integer calculaTempoPAC(Date abertura) {
		if(abertura != null){
			return DateUtil.obterQtdDiasEntreDuasDatasTruncadas(abertura, new Date());
		}else{
			return null;
		}
	}
	
	private boolean isFieldValid(Object param, Object comparator){
		return CoreUtil.modificados(param, comparator);
	}
	
	public boolean isValidForSearch(ConsultarAndamentoProcessoCompraVO filtro) {
		if(		isFieldValid(filtro.getDataGeracaoFinal(), null) || isFieldValid(filtro.getDataGeracaoInicial(), null) ||
				isFieldValid(filtro.getDataVencimentoFinal(), null) || isFieldValid(filtro.getDataVencimentoInicial(), null) ||
				isFieldValid(filtro.getCentroCustoAplicacao(), null) || isFieldValid(filtro.getCentroCustoSolicitante(), null) ||
				isFieldValid(filtro.getComplemento(), null) || isFieldValid(filtro.getFornecedor(), null) ||
				isFieldValid(filtro.getGestor(), "") || isFieldValid(filtro.getGrupoMaterial(), null) ||
				isFieldValid(filtro.getMaterial(), null) || isFieldValid(filtro.getNumeroAF(), null) ||
				isFieldValid(filtro.getNumeroPac(), null) || isFieldValid(filtro.getPacAF(), null) ||
				isFieldValid(filtro.getPacAFPendente(), null) || isFieldValid(filtro.getPacEncerrado(), null) ||
				isFieldValid(filtro.getPacIncompleto(), null) || isFieldValid(filtro.getPacInvestimento(), null) ||
				isFieldValid(filtro.getSc(), null) || isFieldValid(filtro.getSs(), null) ||
				isFieldValid(filtro.getServico(), null) || isFieldValid(filtro.getTipo(), null) ||
				isFieldValid(filtro.getMarcaComercial(), null) || isFieldValid(filtro.getModalidadeLicitacao(), null) ||
				isFieldValid(filtro.getLocal(), null) || isFieldValid(filtro.getObjeto(), "")){
			return true;
		}
		return false;
	}
	
	// C3 - #5460
	public List<EstatisticaPacVO> gerarEstatisticas(){
		
		List<EstatisticaPacVO> listaVo = new ArrayList<EstatisticaPacVO>();
		
		List<Object[]> pendentePorModalidade = this.getScoModalidadeLicitacaoDAO().obterEstatisticaPACsPendentesPorModalidade();
		
		for (Object[] obj : pendentePorModalidade) {
			EstatisticaPacVO vo = new EstatisticaPacVO();
			
			vo.setModalidade(obj[0].toString());
			
			Integer qtde = Integer.parseInt(obj[1].toString()) + Integer.parseInt(obj[2].toString()); 
			vo.setQuantidade(qtde);
			
			listaVo.add(vo);
		}
		
		return listaVo;
		
	}
	
	public List<PACsPendetesVO> obterQtdPACsPendentes() {
		
		List<PACsPendetesVO> listaPACsPendetes = new ArrayList<PACsPendetesVO>();
		
		// C1 - #5460
		Object semAF = this.getScoModalidadeLicitacaoDAO().obterQtdPACsPendentesSemAF();
		
		// C2 - #5460
		Object comAF = this.getScoModalidadeLicitacaoDAO().obterQtdPACsPendentesComAF();
		
		PACsPendetesVO pacSemAF = new PACsPendetesVO();
		pacSemAF.setSituacao("PACs Pendentes Sem AF");
		pacSemAF.setPacsPendentes(Integer.parseInt(semAF.toString()));
		listaPACsPendetes.add(pacSemAF);
		
		PACsPendetesVO pacComAF = new PACsPendetesVO();
		pacComAF.setSituacao("PACs Pendentes Com AF");
		pacComAF.setPacsPendentes(Integer.parseInt(comAF.toString()));
		listaPACsPendetes.add(pacComAF);
		
		PACsPendetesVO pacTotal = new PACsPendetesVO();
		pacTotal.setSituacao("Total");
		Integer total = Integer.parseInt(semAF.toString()) + Integer.parseInt(comAF.toString());
		pacTotal.setPacsPendentes(total);
		listaPACsPendetes.add(pacTotal);
		
		return listaPACsPendetes;
	}	

	public Long consultarAndamentoProcessoCompraCount(ConsultarAndamentoProcessoCompraVO filtro) {
		return getScoAndamentoProcessoCompraDAO().consultarAndamentoProcessoCompraCount(filtro);
	}

	protected ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO() {
		return scoAndamentoProcessoCompraDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO() {
		return scoLocalizacaoProcessoDAO;
	}

	protected ScoModalidadeLicitacaoDAO getScoModalidadeLicitacaoDAO() {
		return scoModalidadeLicitacaoDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
