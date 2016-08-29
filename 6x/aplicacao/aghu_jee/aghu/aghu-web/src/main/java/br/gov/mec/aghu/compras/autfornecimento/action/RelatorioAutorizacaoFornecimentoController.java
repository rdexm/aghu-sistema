package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFJnVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFPVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfEmpenho;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioAutorizacaoFornecimentoController  extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = -5005769024181822851L;

	private static final String RELATORIO_AUTORIZACAO_FORNECIMENTO = "compras-relatorioAutorizacaoFornecimento";
	private static final String IMPRIMIR_AUTORIZACAO_FORNECIMENTO = "compras-imprimirAutorizacaoFornecimento";

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	protected IParametroFacade parametroFacade;
		
	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	//armazena dados do relatório
	private List<RelatorioAFPVO> dados = null;	
	private List<RelatorioAFJnVO> dadosJn = null;	
	private List<RelatorioAFPVO> dadosAfp = null;	
	
	private Integer numPac;
	private Short    nroComplemento;
	private Short  sequenciaAlteracao;
	private Integer  afpNumero;

	private List<AutorizacaoFornPedidosVO> listaSelecionados = new ArrayList<AutorizacaoFornPedidosVO>();
	
	private boolean indPrevia;
	private String itemVersaoAf = "0";
	private boolean habSequencia = true;
	private boolean habVersoes;
	
 		
	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;
	
	private Integer numeroAF;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() throws BaseException, JRException, SystemException, IOException, DocumentException{
				
		if(numPac != null && nroComplemento != null){
			if (sequenciaAlteracao != null){
				itemVersaoAf = "1";
				habilitarSequencia();
			}
			print();
		} 
	}
	
	public String voltar() {
		if(voltarParaUrl != null){
			return voltarParaUrl;
		}
		
		return RELATORIO_AUTORIZACAO_FORNECIMENTO;
	}
	
	public void popularDados() throws ApplicationBusinessException {
		this.dados = null;
		this.dadosJn = null;
		this.dadosAfp = null;
	
		if (itemVersaoAf!=null && itemVersaoAf.equalsIgnoreCase("0")){
			RelatorioAFPVO afVO =  this.autFornecimentoFacade.pesquisarAFsPorLicitacaoComplSeqAlteracao(numPac, nroComplemento);
			if (afVO!=null && afVO.getAutorizacaoForn()!=null){				
				
				if(afVO.getAutorizacaoForn().getPropostaFornecedor().getFornecedor() != null){
					afVO.getAutorizacaoForn().getPropostaFornecedor().setFornecedor(this.comprasFacade.obterScoFornecedorComCidadePorChavePrimaria(afVO.getAutorizacaoForn().getPropostaFornecedor().getFornecedor().getNumero()));
					
				}				
				
				afVO.getAutorizacaoForn().setServidorGestor(buscarServidorPessoa(afVO.getAutorizacaoForn().getServidorGestor()));			
				afVO.getAutorizacaoForn().setServidorAutorizado(buscarServidorPessoa(afVO.getAutorizacaoForn().getServidorAutorizado()));				
				afVO.getAutorizacaoForn().setServidorAssinaCoord(buscarServidorPessoa(afVO.getAutorizacaoForn().getServidorAssinaCoord()));	
				afVO.getAutorizacaoForn().setServidor(buscarServidorPessoa(afVO.getAutorizacaoForn().getServidor()));	
				
				
				this.dados = new ArrayList<RelatorioAFPVO>();
				this.dados.add(afVO);
				// buscar a ultima alteração para setar no campo motivo alteração
				this.dados.get(0).getAutorizacaoForn().setMotivoAlteracaoAf(this.autFornecimentoFacade.buscarUltimoMotivoAlteracao(this.dados.get(0).getAutorizacaoForn().getNumero(), numPac, nroComplemento ));
				numeroAF = afVO.getAutorizacaoForn().getNumero();
			}
		
		} else if (itemVersaoAf!=null && itemVersaoAf.equalsIgnoreCase("1")){
		
			RelatorioAFJnVO afJnVO = this.autFornecimentoFacade.pesquisarJnAFsPorLicitacaoComplSeqAlteracao(numPac, nroComplemento, sequenciaAlteracao);
			if (afJnVO!=null && afJnVO.getAutorizacaoForn()!=null){
				// setar lista de itens
				afJnVO.setListaItensAfVO(this.autFornecimentoFacade.buscarListaItensJn(afJnVO.getAutorizacaoForn()));
				
				if(afJnVO.getAutorizacaoForn().getPropostaFornecedor().getFornecedor() != null){
					afJnVO.getAutorizacaoForn().getPropostaFornecedor().setFornecedor(this.comprasFacade.obterScoFornecedorComCidadePorChavePrimaria(afJnVO.getAutorizacaoForn().getPropostaFornecedor().getFornecedor().getNumero()));
					
				}
				
				afJnVO.getAutorizacaoForn().setServidorAutorizado(buscarServidorPessoa(afJnVO.getAutorizacaoForn().getServidorAutorizado()));				
				afJnVO.getAutorizacaoForn().setServidorAssinaCoord(buscarServidorPessoa(afJnVO.getAutorizacaoForn().getServidorAssinaCoord()));				
				afJnVO.getAutorizacaoForn().setServidor(buscarServidorPessoa(afJnVO.getAutorizacaoForn().getServidor()));	
				
				this.dadosJn = new ArrayList<RelatorioAFJnVO>();
				this.dadosJn.add(afJnVO);
				// buscar a ultima alteração para setar no campo motivo alteração
				this.dadosJn.get(0).getAutorizacaoForn().setMotivoAlteracaoAf(this.autFornecimentoFacade.buscarUltimoMotivoAlteracao(this.dadosJn.get(0).getAutorizacaoForn().getNumero(), numPac, nroComplemento ));
				numeroAF = afJnVO.getAutorizacaoForn().getNumero();
			}
			
		} else if (this.afpNumero!=null){
			Integer pEspecieEmpenhoOrig = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
			
			RelatorioAFPVO afVO =  this.autFornecimentoFacade.pesquisarAFPedidoPorNumEPac(afpNumero, numPac, nroComplemento , pEspecieEmpenhoOrig);
			if (afVO!=null && afVO.getAfp()!=null){						
				
				this.dadosAfp = new ArrayList<RelatorioAFPVO>();
				this.dadosAfp.add(afVO);
				numeroAF = afVO.getAfp().getScoAutorizacaoForn().getNumero();
			}	
		}

	}
	
	public RapServidores buscarServidorPessoa(RapServidores servidor) throws ApplicationBusinessException{
		
		if (servidor != null) {
			servidor = this.registroColaboradorFacade.obterRapServidor(servidor.getId());
		    if(servidor.getPessoaFisica() != null) {
		    	servidor.setPessoaFisica(this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()));
		    }
		}
		
		return servidor;
		
	}
		
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		
		this.popularDados();
			
		if ((getDadosAfp() == null || getDadosAfp().isEmpty()) && (getDadosJn() == null || getDadosJn().isEmpty()) 
				&& (getDados() == null || getDados().isEmpty())) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		
		return null;
		}

		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			return IMPRIMIR_AUTORIZACAO_FORNECIMENTO;

	}	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint()  throws SistemaImpressaoException, ApplicationBusinessException {

		if (listaSelecionados!=null && listaSelecionados.size()>0){
			for (AutorizacaoFornPedidosVO afs : listaSelecionados){
				this.afpNumero = afs.getNumeroAFP();
				this.nroComplemento = afs.getNumeroComplemento();
				this.numPac = afs.getLctNumero();
				this.itemVersaoAf = null;
				this.popularDados();
				this.imprimirPorAfp();
			}
		}else {
			this.popularDados();
			this.imprimirPorAfp();
		}
		
	}
	
	public String printAfp(Integer numAfp, Short nroComplemento, Integer numPac)  throws BaseException, JRException, SystemException, IOException, DocumentException{
		this.afpNumero = numAfp;
		this.nroComplemento = nroComplemento;
		this.numPac = numPac;
		this.itemVersaoAf = null;
		return print();
	}
	
	public void imprimirPorAfp(){
	
		if ((getDadosAfp() == null || getDadosAfp().isEmpty()) && (getDadosJn() == null || getDadosJn().isEmpty()) 
				&& (getDados() == null || getDados().isEmpty())) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			return;
		}
			
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	
	public String printAf(Integer numPac, Short nroComplemento) throws BaseException, JRException, SystemException, IOException, DocumentException{
		this.nroComplemento = nroComplemento;
		this.numPac = numPac;
		this.itemVersaoAf = "0";
		return print();
	}
	
	
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		try {
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/report/");
			params.put("cnpj", 				parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_CGC));
			params.put("razaoSocial",		parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL));
			params.put("endereco", 			parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO));
			params.put("cep", 				parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CEP));
			params.put("caminhoLogoHosp", 	parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_RELATIVO_JEE7));
			params.put("localEntrega", 		parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_LOCAL_ENTREGA));
			params.put("endEntrega", 		parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_ENDERECO_ENTREGA));
			params.put("horEntrega", 		parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_HORARIO_ENTREGA));
			params.put("caminhoLogoUfrgs", 	parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_UNIVERSIDADE_RELATIVO_JEE7));
			params.put("dddFoneHospital", 	parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_DDD_FONE_HOSPITAL));
			params.put("prefixoHospital", 	parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_PREFIXO_FONE_HOSPITAL));
			params.put("pAfpAviso", 		parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AFP_AVISO));
			params.put("previsaoEntrega",   getPrevisaoEntregaParametro());
			
			params.putAll(buscarNumeroListaEmpenho());
					
			
			if (this.dados!=null && this.dados.get(0)!=null){
				params.putAll(this.buscarParametrosAutFornecimento());
			}
			
			else if  (this.dadosJn!=null && this.dadosJn.get(0)!=null){
				params.putAll(this.buscarParametrosAutFornecimentoJn());
			}
			else {
				params.putAll(this.buscarParametrosAutFornecimentoPedido());
			}
			
			
			params.put("indPrevia", indPrevia);
			
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return params;

	}
	
	public Map<String, Object> buscarParametrosAutFornecimento() throws BaseException{
		Map<String, Object> params = new HashMap<String, Object>();
		
		Long nParcelas = this.comprasFacade.pesquisarNumeroParcelasCount(this.dados.get(0).getAutorizacaoForn().getCondicaoPagamentoPropos().getNumero());
		params.put("nParcelas", nParcelas== 0l ? 1 : nParcelas.intValue());
		
		
		try {
			String prazoParcelas = this.autFornecimentoFacade.buscarPrazosAtendimento(this.dados.get(0).getAutorizacaoForn().getCondicaoPagamentoPropos().getNumero());
			if (prazoParcelas!=null){
				params.put("prazoParcelas", prazoParcelas+ " dias");
			}	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
					
		if (this.dados.get(0).getAutorizacaoForn().getAfEmpenho()!=null && this.dados.get(0).getAutorizacaoForn().getAfEmpenho().size()>0){
			params.putAll(this.buscarNumeroListaEmpenho());
		}
		
		if (this.dados.get(0).getAutorizacaoForn().getPropostaFornecedor()!=null && this.dados.get(0).getAutorizacaoForn().getPropostaFornecedor().getFornecedor()!=null
					&& this.dados.get(0).getAutorizacaoForn().getPropostaFornecedor().getFornecedor().getContaCorrenteFornecedor()!=null){
			List<ScoContaCorrenteFornecedor> contasFornecedor = comprasFacade.obterScoContaCorrenteFornecedorPorFornecedor(dados.get(0).getAutorizacaoForn().getPropostaFornecedor().getFornecedor());
			params.putAll(this.buscaParametrosContaCorrente(contasFornecedor));
		}
		
		params.putAll(this.buscarVlrTotalEmpenho(this.dados.get(0).getAutorizacaoForn().getNumero()));
		
		return params;
	}
	
	
	public Map<String, Object>  buscarParametrosAutFornecimentoJn() throws BaseException{
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		Long nParcelas = this.comprasFacade.pesquisarNumeroParcelasCount(this.dadosJn.get(0).getAutorizacaoForn().getCondicaoPagamentoPropos().getNumero());
		params.put("nParcelas", nParcelas==0l ? 1 : nParcelas);
		
		try {
			String prazoParcelas = this.autFornecimentoFacade.buscarPrazosAtendimento(this.dadosJn.get(0).getAutorizacaoForn().getCondicaoPagamentoPropos().getNumero());
			if (prazoParcelas!=null){
			    params.put("prazoParcelas", prazoParcelas+ " dias");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
					
		if (this.dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor()!=null && this.dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor().getFornecedor()!=null
					&& this.dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor().getFornecedor().getContaCorrenteFornecedor()!=null){
			List<ScoContaCorrenteFornecedor> contasFornecedor = comprasFacade.obterScoContaCorrenteFornecedorPorFornecedor(dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor().getFornecedor());
			params.putAll(this.buscaParametrosContaCorrente(contasFornecedor));
		}
		
		params.putAll(this.buscarVlrTotalEmpenho(this.dadosJn.get(0).getAutorizacaoForn().getNumero()));
		
		return params;
		
	}
	
	public Map<String, Object>  buscarParametrosAutFornecimentoPedido() throws BaseException{
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		try {
			String prazoParcelas = this.autFornecimentoFacade.buscarPrazosAtendimento(this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getCondicaoPagamentoPropos().getNumero());
			if (prazoParcelas!=null){
				params.put("prazoParcelas", prazoParcelas+ " dias");
			}	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
		
		if (this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor()!=null && 
				this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor() !=null
				&& this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getContaCorrenteFornecedor()!=null){
			List<ScoContaCorrenteFornecedor> contasFornecedor = comprasFacade.obterScoContaCorrenteFornecedorPorFornecedor(this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor());			
			params.putAll(this.buscaParametrosContaCorrente(contasFornecedor));
	}
		
		return params;
	}
	
	public Map<String, Object> buscarNumeroListaEmpenho() throws BaseException{
		Map<String, Object> params = new HashMap<String, Object>();
		Integer numAf = Integer.valueOf(0);
		Integer pEspecieEmpenho = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
		
		if (this.dados!=null){
			numAf = this.dados.get(0).getAutorizacaoForn().getNumero();
		}else if (this.dadosJn!=null){
			numAf = this.dadosJn.get(0).getAutorizacaoForn().getNumero();
		}
		else {
			numAf = this.dadosAfp.get(0).getAfp().getScoAutorizacaoForn().getNumero();
		}
		
		Integer numSeqAf = this.autFornecimentoFacade.obterMaxNumEmpenhoPorAfeEspecie(numAf, pEspecieEmpenho);
		
		if (numSeqAf!=null){
			ScoAfEmpenho afEmpenho = this.autFornecimentoFacade.obterEmpenhoPorChavePrimaria(numSeqAf);
			String numeroEmpenho = (afEmpenho!=null && afEmpenho.getAnoEmpenho()!=null?afEmpenho.getAnoEmpenho():"")+ "/ "+afEmpenho.getNumero();
			params.put("numeroEmpenho", numeroEmpenho);
			
			String listaEmpenho = (afEmpenho!=null && afEmpenho.getAnoListaItensAfSiafi()!=null?
								afEmpenho.getAnoListaItensAfSiafi():"")+ "/ "+ (afEmpenho!=null && afEmpenho.getNroListaItensAfSiafi()!=null?
								afEmpenho.getNroListaItensAfSiafi():"");
			params.put("listaEmpenho", listaEmpenho);
		}

		return params;
		
	}
	
	public Map<String, Object> buscarVlrTotalEmpenho(Integer numAf){
		Map<String, Object> params = new HashMap<String, Object>();
		Double vlrTotalEmpenho = 0.0;
		Double vlrEmpenhoEsp1 = 0.0;
		Double vlrEmpenhoEsp2 = 0.0;
		Double vlrEmpenhoEsp3 = 0.0;
		
		try {
			
			Integer pEspecieEmpenhoOrig = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
			Integer pEspecieEmpenhoRef = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_ESPECIE_EMPENHO_REF);
			Integer pEspecieEmpenhoAnul = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ANUL);
			
			List<ScoAfEmpenho>  listaVlrEmpenho = this.autFornecimentoFacade.buscarEmpenhoPorAfNum(numAf);
			if (listaVlrEmpenho!=null){
				for (ScoAfEmpenho afEmpenho:listaVlrEmpenho){
					if (afEmpenho.getEspecie().intValue()== pEspecieEmpenhoOrig){
						vlrEmpenhoEsp1 = vlrEmpenhoEsp1 + afEmpenho.getValor();
					}
					if (afEmpenho.getEspecie().intValue()== pEspecieEmpenhoRef){
						vlrEmpenhoEsp2 = vlrEmpenhoEsp2 + afEmpenho.getValor();
					}
					if (afEmpenho.getEspecie().intValue()== pEspecieEmpenhoAnul){
						vlrEmpenhoEsp3 = vlrEmpenhoEsp3 + afEmpenho.getValor();
					}
				}
			}
			
			vlrTotalEmpenho = (vlrEmpenhoEsp1 + vlrEmpenhoEsp2) - vlrEmpenhoEsp3;
			params.put("vlrTotalEmpenho", vlrTotalEmpenho);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return params;
		
	}
	
	public Map<String, Object> buscaParametrosContaCorrente(List<ScoContaCorrenteFornecedor> listaCcForncecedor){
		 
		Map<String, Object> params = new HashMap<String, Object>();
		 
		for(ScoContaCorrenteFornecedor ccForn: listaCcForncecedor){			
			
			 if (ccForn.getIndPreferencial()== DominioSimNao.S){
				 params.put("bcoCodigo", ccForn.getId().getAgbBcoCodigo().toString());
				 params.put("agCodigo", ccForn.getId().getAgbCodigo().toString());
				 params.put("contaCorrente", ccForn.getId().getContaCorrente());
			 }
			 
		}
		 
		 return params;
	 }
	 
	public Date getPrevisaoEntregaParametro(){
		
		GregorianCalendar gcal = new GregorianCalendar();  
		gcal.setTime(new Date());
		
		if (this.dados!=null && this.dados.get(0)!=null){
			if (this.dados.get(0).getAutorizacaoForn().getPropostaFornecedor()!=null && this.dados.get(0).getAutorizacaoForn().getPropostaFornecedor().getPrazoEntrega()!=null){
			 	 gcal.add(Calendar.DAY_OF_MONTH, this.dados.get(0).getAutorizacaoForn().getPropostaFornecedor().getPrazoEntrega()+5);
			}
			
			 
		} else if (this.dadosJn!=null && this.dadosJn.get(0)!=null){
			if (this.dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor()!=null && this.dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor().getPrazoEntrega()!=null){
			 	 gcal.add(Calendar.DAY_OF_MONTH, this.dadosJn.get(0).getAutorizacaoForn().getPropostaFornecedor().getPrazoEntrega()+5);
			}
		} else {
			gcal.add(Calendar.DAY_OF_MONTH, 5);
		}
		
		return  gcal.getTime();
	}
	
	public void habilitarVersoes(){
		if (this.afpNumero!=null){
			this.itemVersaoAf = null;
			this.sequenciaAlteracao = null;
			this.habSequencia = true;
			this.habVersoes = true;
		} else {
			this.habVersoes = false;
			this.itemVersaoAf = "0";
		}
		
	}
	
	public void habilitarSequencia(){
		
		if (itemVersaoAf==null || itemVersaoAf.equalsIgnoreCase("0")){
			this.sequenciaAlteracao = null;
			this.habSequencia = true;
		}
		else {
			this.habSequencia = false;
		}
		
	}

	@Override
	public String recuperarArquivoRelatorio() {
		if (this.afpNumero!=null){
			return "br/gov/mec/aghu/compras/report/AutorizacaoFornecimentoPedido.jasper";
		}
		
		return "br/gov/mec/aghu/compras/report/AutorizacaoFornecimento.jasper";
	}
	
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		if (getDados()!=null){
			return getDados();
			
		} else if (getDadosJn()!=null){
			
			if (this.dadosJn.get(0).getAutorizacaoForn().getItensAutorizacaoForn()!=null){
				for (ScoItemAutorizacaoFornJn jn: this.dadosJn.get(0).getAutorizacaoForn().getItensAutorizacaoForn()){
					jn.getAfnNumero();
				}
			}
			
			return getDadosJn();
			
		} else if (getDadosAfp()!=null){
			return getDadosAfp();
			
		} else { return null;	}
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public void limpar() {
		this.numPac = null;
		this.nroComplemento = null;
		this.sequenciaAlteracao = null;
		this.afpNumero = null;
		this.habVersoes = false;
	}
	
	public void gerarArquivoPDF() throws ApplicationBusinessException, IOException, JRException, DocumentException{
		 this.popularDados();
		 DocumentoJasper documento = gerarDocumento();
		  Date dataAtual = new Date();
		  SimpleDateFormat sdf_1 = new SimpleDateFormat("ddMMyyHHmm");
		
	      File file = new java.io.File("/opt/aghu/afp/AFP_" + numeroAF + "_" +  (this.getAfpNumero() != null ? this.getAfpNumero() + "_" : "")  + sdf_1.format(dataAtual) + ".pdf");  
		  FileOutputStream in = new FileOutputStream(file) ;    
		  in.write(documento.getPdfByteArray(false));  
		  in.close();		 
	}
	
	public String imprimirAutorizacaoFron(){
		return IMPRIMIR_AUTORIZACAO_FORNECIMENTO;
	}

	public Collection<RelatorioAFPVO> getDados() {
		return dados;
	}

	public void setDados(List<RelatorioAFPVO> dados) {
		this.dados = dados;
	}

	public Collection<RelatorioAFJnVO> getDadosJn() {
		return dadosJn;
	}

	public void setDadosJn(List<RelatorioAFJnVO> dadosJn) {
		this.dadosJn = dadosJn;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public boolean isIndPrevia() {
		return indPrevia;
	}

	public void setIndPrevia(boolean indPrevia) {
		this.indPrevia = indPrevia;
	}

	public String getItemVersaoAf() {
		return itemVersaoAf;
	}

	public void setItemVersaoAf(String itemVersaoAf) {
		this.itemVersaoAf = itemVersaoAf;
	}

	public boolean isHabSequencia() {
		return habSequencia;
	}

	public void setHabSequencia(boolean habSequencia) {
		this.habSequencia = habSequencia;
	}

	public boolean isHabVersoes() {
		return habVersoes;
	}

	public void setHabVersoes(boolean habVersoes) {
		this.habVersoes = habVersoes;
	}

	public List<RelatorioAFPVO> getDadosAfp() {
		return dadosAfp;
	}

	public void setDadosAfp(List<RelatorioAFPVO> dadosAfp) {
		this.dadosAfp = dadosAfp;
	}

	public Integer getNumPac() {
		return numPac;
	}

	public void setNumPac(Integer numPac) {
		this.numPac = numPac;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Short getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public Integer getAfpNumero() {
		return afpNumero;
	}

	public void setAfpNumero(Integer afpNumero) {
		this.afpNumero = afpNumero;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
}