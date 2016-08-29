package br.gov.mec.aghu.compras.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioOpcoesResultadoAF;
import br.gov.mec.aghu.dominio.DominioProgrGeralEntregaAF;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PesquisaGeralAFON extends BaseBusiness {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String FORNECEDOR = "Fornecedor";
	private static final String CNPJ_CPF = "CNPJ/CPF";
	private static final String GESTOR = "Gestor";
	private static final String MATRICULA = "Matricula";
	private static final String CP = "CP";
	private static final String NRO_AF = "Nro AF";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5922834917785524834L;
	private static final String SEPARADOR = ";";
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final String PREFIXO = "PESQAF_";
	private static final String PROGRAMACAO_GERAL_ENTREGA = "PROGRAMACAO_GERAL_ENTREGA_";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String EXTENSAO = ".csv";
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;	
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private static final Log LOG = LogFactory.getLog(PesquisaGeralAFON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
		

	public enum PesquisaGeralAFONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CAMPO_OBRIGATORIO_AUTORIZACOES_FORNECIMENTO, MENSAGEM_ERRO_DATA_FINAL_INVALIDA_AUTORIZACOES_FORNECIMENTO;
	}

	public void validaPesquisaGeralAF(FiltroPesquisaGeralAFVO filtro)
			throws ApplicationBusinessException {

		ajustaHoraDataFinal(filtro);
		validaPeriodoDatas(filtro);

		if (filtro.getNumeroAF() == null
				&& filtro.getComplemento() == null
				&& filtro.getSequencia() == null
				&& filtro.getNumeroAFP() == null
				&& filtro.getItem() == null
				&& filtro.getModalidadeCompra() == null
				&& filtro.getServidorGestor() == null
				&& filtro.getSituacao() == null
				&& filtro.getDataGeracaoInicial() == null
				&& filtro.getDataGeracaoFinal() == null
				&& filtro.getPrevisaoEntregaInicial() == null
				&& filtro.getPrevisaoEntregaFinal() == null
				&& filtro.getModalidadeEmpenho() == null
				&& filtro.getFornecedor() == null
				&& (filtro.getTermoLivre() == null || filtro.getTermoLivre()
						.equals("")) && filtro.getGrupoMaterial() == null
				&& filtro.getMaterial() == null
				&& filtro.getGrupoServico() == null
				&& filtro.getServico() == null
				&& filtro.getCentroCustoSolicitante() == null
				&& filtro.getCentroCustoAplicacao() == null
				&& filtro.getVerbaGestao() == null
				&& filtro.getGrupoNaturezaDespesa() == null
				&& filtro.getNaturezaDespesa() == null
				&& filtro.getNumeroContrato() == null
				&& filtro.getEntregaAtrasada().equals(Boolean.FALSE)) {

			throw new ApplicationBusinessException(
					PesquisaGeralAFONExceptionCode.MENSAGEM_ERRO_CAMPO_OBRIGATORIO_AUTORIZACOES_FORNECIMENTO);
		}
	}

	private void validaPeriodoDatas(FiltroPesquisaGeralAFVO filtro)
			throws ApplicationBusinessException {
		if (filtro.getDataGeracaoInicial() != null
				&& filtro.getDataGeracaoFinal() != null) {
			if (!DateUtil.validaDataMaiorIgual(filtro.getDataGeracaoFinal(),
					filtro.getDataGeracaoInicial())) {
				throw new ApplicationBusinessException(
						PesquisaGeralAFONExceptionCode.MENSAGEM_ERRO_DATA_FINAL_INVALIDA_AUTORIZACOES_FORNECIMENTO);

			}
		}

		if (filtro.getPrevisaoEntregaInicial() != null
				&& filtro.getPrevisaoEntregaFinal() != null) {
			if (!DateUtil.validaDataMaiorIgual(
					filtro.getPrevisaoEntregaFinal(),
					filtro.getPrevisaoEntregaInicial())) {
				throw new ApplicationBusinessException(
						PesquisaGeralAFONExceptionCode.MENSAGEM_ERRO_DATA_FINAL_INVALIDA_AUTORIZACOES_FORNECIMENTO);

			}
		}
	}

	private void ajustaHoraDataFinal(FiltroPesquisaGeralAFVO filtro) {
		if (filtro.getDataGeracaoInicial() != null
				&& filtro.getDataGeracaoFinal() != null) {
			Calendar calendarDtFinalRef = Calendar.getInstance();
			calendarDtFinalRef.setTime(filtro.getDataGeracaoFinal());
			calendarDtFinalRef.set(Calendar.HOUR_OF_DAY, 23);
			calendarDtFinalRef.set(Calendar.MINUTE, 59);
			calendarDtFinalRef.set(Calendar.SECOND, 59);
			Date dataGeracaoFinalAjustada = calendarDtFinalRef.getTime();

			filtro.setDataGeracaoFinal(dataGeracaoFinalAjustada);
		}

		if (filtro.getPrevisaoEntregaInicial() != null
				&& filtro.getPrevisaoEntregaFinal() != null) {
			Calendar calendarDtFinalRef = Calendar.getInstance();
			calendarDtFinalRef.setTime(filtro.getPrevisaoEntregaFinal());
			calendarDtFinalRef.set(Calendar.HOUR_OF_DAY, 23);
			calendarDtFinalRef.set(Calendar.MINUTE, 59);
			calendarDtFinalRef.set(Calendar.SECOND, 59);
			Date previsaoEntregaFinalAjustada = calendarDtFinalRef.getTime();

			filtro.setPrevisaoEntregaFinal(previsaoEntregaFinalAjustada);
		}
	}

	public List<PesquisaGeralAFVO> listarAutorizacoesFornecimentoFiltrado(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro) {
		List<PesquisaGeralAFVO> resultado = null;
		resultado = getAutFornecimentoFacade()
				.listarAutorizacoesFornecimentoFiltrado(firstResult,
						maxResults, orderProperty, asc, filtro);

		for (PesquisaGeralAFVO vo : resultado) {
			// Seta o servidorGestor
			RapServidoresId id = new RapServidoresId(vo.getMatricula(),
					vo.getVinCodigo());
			RapServidores servidorGestor = getRegistroColaboradorFacade()
					.obterRapServidor(id);
			vo.setServidorGestor(servidorGestor);

			// Seta o fornecedor
			ScoFornecedor fornecedor = getScoFornecedorDAO()
					.obterPorChavePrimaria(vo.getFrnNumero());
			vo.setFornecedor(fornecedor);
			Hibernate.initialize(vo.getModalidadeLicitacao());
			if(vo.getServidorGestor()!= null){
				Hibernate.initialize(vo.getServidorGestor().getPessoaFisica());
			}
		}

		return resultado;
	}

	public List<PesquisaGeralIAFVO> listarItensAutorizacoesFornecimentoFiltrado(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro) {
		List<PesquisaGeralIAFVO> resultado = null;
		resultado = getAutFornecimentoFacade()
				.listarItensAutorizacoesFornecimentoFiltrado(firstResult,
						maxResults, orderProperty, asc, filtro);

		for (PesquisaGeralIAFVO vo : resultado) {
			// Seta o servidorGestor
			RapServidoresId id = new RapServidoresId(vo.getMatricula(),
					vo.getVinCodigo());
			RapServidores servidorGestor = getRegistroColaboradorFacade()
					.obterRapServidor(id);
			vo.setServidorGestor(servidorGestor);

			// Seta o fornecedor
			ScoFornecedor fornecedor = getScoFornecedorDAO()
					.obterPorChavePrimaria(vo.getFrnNumero());
			vo.setFornecedor(fornecedor);

			// Seta o saldo
			vo.setSaldo((vo.getQtdeSolicitada() != null ? vo
					.getQtdeSolicitada() : 0)
					- (vo.getQtdeRecebida() != null ? vo.getQtdeRecebida() : 0));

			// Seta o valor total
			if (vo.getMaterial() != null) {
				vo.setValorTotal((vo.getQtdeRecebida() != null ? vo
						.getQtdeRecebida() : 0)
						* (vo.getValorUnitario() != null ? vo
								.getValorUnitario() : 0));

			} else if (vo.getServico() != null) {
				vo.setValorTotal(vo.getValorUnitario());
			}
			this.carregarCamposVO(vo);
		}

		return resultado;
	}
	
	private void carregarCamposVO(PesquisaGeralIAFVO vo){
		Hibernate.initialize(vo.getModalidadeLicitacao());
		Hibernate.initialize(vo.getFornecedor());
		Hibernate.initialize(vo.getMaterial());
		Hibernate.initialize(vo.getServico());
		if(vo.getServidorGestor()!= null){
			Hibernate.initialize(vo.getServidorGestor().getPessoaFisica());
		}
	}

	public List<PesquisaGeralPAFVO> listarPedidosAutorizacoesFornecimentoFiltrado(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro) {
		List<PesquisaGeralPAFVO> resultado = null;
		resultado = getAutFornecimentoFacade()
				.listarPedidosAutorizacoesFornecimentoFiltrado(firstResult,
						maxResults, orderProperty, asc, filtro);

		for (PesquisaGeralPAFVO vo : resultado) {
			// Seta o servidorGestor
			RapServidoresId id = new RapServidoresId(vo.getMatricula(),
					vo.getVinCodigo());
			RapServidores servidorGestor = getRegistroColaboradorFacade()
					.obterRapServidor(id);
			vo.setServidorGestor(servidorGestor);

			// Seta o fornecedor
			ScoFornecedor fornecedor = getScoFornecedorDAO()
					.obterPorChavePrimaria(vo.getFrnNumero());
			vo.setFornecedor(fornecedor);
			if(vo.getServidorGestor()!= null){
				Hibernate.initialize(vo.getServidorGestor().getPessoaFisica());
			}
			Hibernate.initialize(vo.getFornecedor());
		}

		return resultado;
	}

	public List<PesquisaGeralPIAFVO> listarParcelasItensAutorizacoesFornecimentoFiltrado(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro) {
		List<PesquisaGeralPIAFVO> resultado = null;
		resultado = getAutFornecimentoFacade()
				.listarParcelasItensAutorizacoesFornecimentoFiltrado(
						firstResult, maxResults, orderProperty, asc, filtro);

		for (PesquisaGeralPIAFVO vo : resultado) {
			// Seta o servidorGestor
			RapServidoresId id = new RapServidoresId(vo.getMatricula(),
					vo.getVinCodigo());
			RapServidores servidorGestor = getRegistroColaboradorFacade()
					.obterRapServidor(id);
			vo.setServidorGestor(servidorGestor);

			// Seta o fornecedor
			ScoFornecedor fornecedor = getScoFornecedorDAO()
					.obterPorChavePrimaria(vo.getFrnNumero());
			vo.setFornecedor(fornecedor);

			// Seta o saldo
			vo.setSaldo((vo.getQtde() != null ? vo.getQtde() : 0)
					- (vo.getQtdeEntregue() != null ? vo.getQtdeEntregue() : 0));
			
			Hibernate.initialize(vo.getModalidadeLicitacao());
			if(vo.getServidorGestor()!= null){
				Hibernate.initialize(vo.getServidorGestor().getPessoaFisica());
			}
			org.hibernate.Hibernate.initialize(vo.getMaterial());
			Hibernate.initialize(vo.getServico());
		}

		return resultado;
	}

	public String geraArquivoConsultaPorAF(FiltroPesquisaGeralAFVO filtro)
			throws IOException {
		File file = File.createTempFile(PREFIXO, EXTENSAO);

		Writer out = new OutputStreamWriter(new FileOutputStream(file),ISO_8859_1);

		// GERA CABEÇALHOS DO CSV
		out.write(geraCabecalho(DominioOpcoesResultadoAF.POR_AF));

		List<PesquisaGeralAFVO> consultas = listarAutorizacoesFornecimentoFiltrado(
				null, null, null, false, filtro);

		// ESCREVE LINHAS DO CSV
		for (PesquisaGeralAFVO consulta : consultas) {
			out.write(LINE_SEPARATOR);
			out.write(geraLinhaConsultaPorAF(consulta));
		}
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	public String geraArquivoConsultaPorIAF(FiltroPesquisaGeralAFVO filtro)
			throws IOException {
		File file = File.createTempFile(PREFIXO, EXTENSAO);

		Writer out = new OutputStreamWriter(new FileOutputStream(file),
				ISO_8859_1);
		out.write(geraCabecalho(DominioOpcoesResultadoAF.POR_ITEM_AF));

		List<PesquisaGeralIAFVO> consultas = listarItensAutorizacoesFornecimentoFiltrado(
				null, null, null, false, filtro);

		for (PesquisaGeralIAFVO consulta : consultas) {
			out.write(LINE_SEPARATOR);
			out.write(geraLinhaConsultaPorIAF(consulta));
		}
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	public String geraArquivoConsultaPorPAF(FiltroPesquisaGeralAFVO filtro)
			throws IOException {
		File file = File.createTempFile(PREFIXO, EXTENSAO);

		Writer out = new OutputStreamWriter(new FileOutputStream(file),
				ISO_8859_1);
		out.write(geraCabecalho(DominioOpcoesResultadoAF.POR_AFP));

		List<PesquisaGeralPAFVO> consultas = listarPedidosAutorizacoesFornecimentoFiltrado(
				null, null, null, false, filtro);

		for (PesquisaGeralPAFVO consulta : consultas) {
			out.write(LINE_SEPARATOR);
			out.write(geraLinhaConsultaPorPAF(consulta));
		}
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	public String geraArquivoConsultaPorPIAF(FiltroPesquisaGeralAFVO filtro)
			throws IOException {
		File file = File.createTempFile(PREFIXO, EXTENSAO);

		Writer out = new OutputStreamWriter(new FileOutputStream(file),
				ISO_8859_1);
		out.write(geraCabecalho(DominioOpcoesResultadoAF.POR_PARCELA));

		List<PesquisaGeralPIAFVO> consultas = listarParcelasItensAutorizacoesFornecimentoFiltrado(
				null, null, null, false, filtro);

		for (PesquisaGeralPIAFVO consulta : consultas) {
			out.write(LINE_SEPARATOR);
			out.write(geraLinhaConsultaPorPIAF(consulta));
		}
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	public String geraCabecalho(DominioOpcoesResultadoAF tipoConsulta) {
		if (tipoConsulta.equals(DominioOpcoesResultadoAF.POR_AF)) {
			return NRO_AF + SEPARADOR + CP + SEPARADOR + "Seq Alt"
					+ SEPARADOR + "Mod PAC" + SEPARADOR + "Situacao"
					+ SEPARADOR + "Geracao" + SEPARADOR + MATRICULA
					+ SEPARADOR + GESTOR + SEPARADOR + "Andamento"
					+ SEPARADOR + CNPJ_CPF + SEPARADOR + FORNECEDOR;

		} else if (tipoConsulta.equals(DominioOpcoesResultadoAF.POR_ITEM_AF)) {
			return NRO_AF + SEPARADOR + CP + SEPARADOR + "Seq Alt"
					+ SEPARADOR + "Item" + SEPARADOR + "Codigo" + SEPARADOR
					+ "Material/Servico" + SEPARADOR + "Unidade" + SEPARADOR
					+ "Qtde Solic" + SEPARADOR + "Qtde Rec" + SEPARADOR
					+ "Saldo" + SEPARADOR + "Vl Total" + SEPARADOR + CNPJ_CPF
					+ SEPARADOR + FORNECEDOR + SEPARADOR + MATRICULA
					+ SEPARADOR + GESTOR + SEPARADOR + "Situacao" + SEPARADOR
					+ "Mod PAC";

		} else if (tipoConsulta.equals(DominioOpcoesResultadoAF.POR_AFP)) {
			return NRO_AF + SEPARADOR + CP + SEPARADOR + "AFP" + SEPARADOR
					+ "Data Envio" + SEPARADOR + MATRICULA + SEPARADOR
					+ GESTOR + SEPARADOR + CNPJ_CPF + SEPARADOR + FORNECEDOR;

		} else if (tipoConsulta.equals(DominioOpcoesResultadoAF.POR_PARCELA)) {
			return NRO_AF + SEPARADOR + CP + SEPARADOR + "Item" + SEPARADOR
					+ "Parcela" + SEPARADOR + "Codigo" + SEPARADOR
					+ "Material/Servico" + SEPARADOR + "Unidade" + SEPARADOR
					+ "Qtde Solic" + SEPARADOR + "Qtde Rec" + SEPARADOR
					+ "Saldo" + SEPARADOR + "Vl Total" + SEPARADOR + CNPJ_CPF
					+ SEPARADOR + FORNECEDOR + SEPARADOR + MATRICULA
					+ SEPARADOR + GESTOR + SEPARADOR + "Situacao" + SEPARADOR
					+ "Mod PAC";
		}
		return null;
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	public String geraLinhaConsultaPorAF(PesquisaGeralAFVO vo) {
		StringBuilder texto = new StringBuilder();
		addText(vo.getLctNumero(), texto);
		addText(vo.getNroComplemento(), texto);
		addText(vo.getSequenciaAlteracao(), texto);
		addText(vo.getModalidadeLicitacao() != null ? vo.getModalidadeLicitacao().getDescricao().replace(";"," ") : "", texto);
		addText(vo.getSituacao() != null ? vo.getSituacao().getDescricao().replace(";"," ") : "", texto);
		addText(DateUtil.obterDataFormatada(vo.getDtGeracao(), DD_MM_YYYY),texto);
		addText(vo.getMatricula(), texto);
		addText(obterStringServidorGestor(vo.getServidorGestor()), texto);
		addText("Assinada", texto);
		String cgc = " ";
		if(vo.getFornecedor().getCgc() != null){
			cgc = CoreUtil.formatarCNPJ(vo.getFornecedor().getCgc());
		}else if(vo.getFornecedor().getCpf() != null){
			cgc = CoreUtil.formataCPF(vo.getFornecedor().getCpf());
		}
		addText(cgc, texto);
		addText(vo.getFornecedor() != null ? vo.getFornecedor().getRazaoSocial().replace(";"," ") : "", texto);

		return texto.toString();
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	public String geraLinhaConsultaPorIAF(PesquisaGeralIAFVO vo) {
		StringBuilder texto = new StringBuilder();
		addText(vo.getLctNumero(), texto);
		addText(vo.getNroComplemento(), texto);
		addText(vo.getSequenciaAlteracao(), texto);
		addText(vo.getIafNumero(), texto);
		addText(vo.getMaterial() != null && vo.getMaterial().getCodigo() != null ? vo.getMaterial().getCodigo() : " ", texto);
		addText(obterServMaterial(vo.getMaterial(), vo.getServico()) , texto);
		addText(vo.getUnidadeCodigo(), texto);
		addText(vo.getQtdeSolicitada(), texto);
		addText(vo.getQtdeRecebida(), texto);
		addText(vo.getSaldo() != null ? vo.getSaldo().toString().replace(";"," ") : "", texto);
		addText(obterValorFormatado(vo.getValorTotal()), texto);
		String cgc = " ";
		if(vo.getFornecedor().getCgc() != null){
			cgc = CoreUtil.formatarCNPJ(vo.getFornecedor().getCgc());
		}else if(vo.getFornecedor().getCpf() != null){
			cgc = CoreUtil.formataCPF(vo.getFornecedor().getCpf());
		}
		addText(cgc, texto);
		addText(vo.getFornecedor() != null && vo.getFornecedor().getRazaoSocial() != null ? vo.getFornecedor().getRazaoSocial().replace(";"," ") : "", texto);
		addText(vo.getMatricula(), texto);
		addText(obterStringServidorGestor(vo.getServidorGestor()), texto);
		addText(vo.getSituacao() != null && vo.getSituacao().getDescricao() != null ? vo.getSituacao().getDescricao().replace(";"," ") : " ", texto);
		addText(vo.getModalidadeLicitacao() != null && vo.getModalidadeLicitacao().getDescricao() != null ? vo.getModalidadeLicitacao().getDescricao().replace(";"," ") : "", texto);
		return texto.toString();
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	public String geraLinhaConsultaPorPAF(PesquisaGeralPAFVO vo) {
		StringBuilder texto = new StringBuilder();
		addText(vo.getLctNumero(), texto);
		addText(vo.getNroComplemento(), texto);
		addText(vo.getAfpNumero(), texto);
		addText(DateUtil.obterDataFormatada(vo.getDtEnvioFornecedor(),DD_MM_YYYY), texto);
		addText(vo.getMatricula(), texto);
		addText(obterStringServidorGestor(vo.getServidorGestor()), texto);
		String cgc = " ";
		if(vo.getFornecedor().getCgc() != null){
			cgc = CoreUtil.formatarCNPJ(vo.getFornecedor().getCgc());
		}else if(vo.getFornecedor().getCpf() != null){
			cgc = CoreUtil.formataCPF(vo.getFornecedor().getCpf());
		}
		addText(cgc, texto);
		addText(vo.getFornecedor() != null ? vo.getFornecedor().getRazaoSocial().replace(";"," ") : "", texto);
		return texto.toString();
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	public String geraLinhaConsultaPorPIAF(PesquisaGeralPIAFVO vo) {
		StringBuilder texto = new StringBuilder();
		addText(vo.getLctNumero(), texto);
		addText(vo.getNroComplemento(), texto);
		addText(vo.getIafNumero(), texto);
		addText(vo.getParcela(), texto);
		addText(vo.getMaterial() != null && vo.getMaterial().getCodigo() != null ? vo.getMaterial().getCodigo() : " ", texto);
		addText(obterServMaterial(vo.getMaterial(), vo.getServico()), texto);
		addText(vo.getUnidadeCodigo(), texto);
		addText(vo.getQtde(), texto);
		addText(vo.getQtdeEntregue(), texto);
		addText(vo.getSaldo(), texto);
		addText(obterValorFormatado(vo.getValorEfetivado()), texto);
		String cgc = " ";
		if(vo.getFornecedor().getCgc() != null){
			cgc = CoreUtil.formatarCNPJ(vo.getFornecedor().getCgc());
		}else if(vo.getFornecedor().getCpf() != null){
			cgc = CoreUtil.formataCPF(vo.getFornecedor().getCpf());
		}
		addText(cgc, texto);
		addText(vo.getFornecedor() != null  && vo.getFornecedor().getRazaoSocial() != null ? vo.getFornecedor().getRazaoSocial().replace(";"," ") : "", texto);
		addText(vo.getMatricula(), texto);
		addText(obterStringServidorGestor(vo.getServidorGestor()), texto);
		addText(vo.getSituacao() != null && vo.getSituacao().getDescricao() != null ? vo.getSituacao().getDescricao().replace(";"," ") : "", texto);
		addText(vo.getModalidadeLicitacao() != null && vo.getModalidadeLicitacao().getDescricao() != null ? 
				vo.getModalidadeLicitacao().getDescricao().replace(";"," ") : "", texto);

		return texto.toString();
	}

	private void addText(Object texto, StringBuilder sb) {
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(SEPARADOR);
	}

	private String obterStringServidorGestor(RapServidores servidorGestor) {
		if (servidorGestor != null) {
			return servidorGestor.getPessoaFisica().getNome().replace(";"," ");
		}
		return null;
	}

	private String obterServMaterial(ScoMaterial material, ScoServico servico) {
		String retorno = "";
		if (material != null) {
			retorno = material.getNome();
		} else if (servico != null) {
			retorno = servico.getNome();
		}
		return retorno.replace(";"," ");
	}

	public String obterValorFormatado(Double valor) {
		if (valor != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			Double vlr = NumberUtil.truncateHALFEVEN(valor, 2);
			return df.format(vlr);
		}
		return null;
	}
	
	public File getGerarArquivoCSVConsultaProgramacaoGeralEntregaAF(
			List<ProgrGeralEntregaAFVO> listagem) throws ApplicationBusinessException,
			IOException {
		File file = File.createTempFile(PROGRAMACAO_GERAL_ENTREGA, EXTENSAO);
		Writer out = new OutputStreamWriter(new FileOutputStream(file),ISO_8859_1);

		out.write(gerarCabecalhoCSVConsultaProgramacaoGeralEntregaAF());

		for (ProgrGeralEntregaAFVO programacao : listagem) {
			out.write(LINE_SEPARATOR);
			out.write(gerarLinhaProgramacaoGeralEntregaAF(programacao));
		}
		out.flush();
		out.close();

		return file;
	}

	private String gerarCabecalhoCSVConsultaProgramacaoGeralEntregaAF() {
		StringBuilder result = new StringBuilder();
		for (DominioProgrGeralEntregaAF progrGeralEntregaAF : DominioProgrGeralEntregaAF
				.values()) {
			result.append(progrGeralEntregaAF.getDescricao() + SEPARADOR);
		}
		return result.toString();
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	private String gerarLinhaProgramacaoGeralEntregaAF(
			ProgrGeralEntregaAFVO programacao) {
		StringBuilder texto = new StringBuilder();
		addText(programacao.getAf(), texto);
		addText(programacao.getCp(), texto);
		addText(programacao.getAfp(), texto);
		addText(programacao.getItemAF(), texto);
		addText(programacao.getParcela(), texto);
		addText(DateUtil.obterDataFormatada(programacao.getPrevisaoDt(),DD_MM_YYYY), texto);
		addText(programacao.getQtdParcela(), texto);
		addText(programacao.getUmdForn(), texto);
		addText(programacao.getFatorConv(), texto);
		addText(programacao.getUmdMat(), texto);
		Double quantidadeAReceber = programacao.getQtdReceber();
		addText(quantidadeAReceber!=null?quantidadeAReceber.intValue():0, texto);
		addText(programacao.getAlm(), texto);
		addText(programacao.getEspacoDisponivel(), texto);
		addText(programacao.getCodGrupoMat(), texto);
		addText(programacao.getClassABC(), texto);
		addText(programacao.getMatServ(), texto);
		addText(programacao.getFornecedor(), texto);

		addText(programacao.getIndEmp().equals(DominioAfEmpenhada.S) ? DominioSimNao.S.getDescricao(): DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndCanc() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndPlan() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndAss() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndEnvioForn() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndRecalculoAutomatico() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndRecalculoManual() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndEntregaImediata() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(programacao.getIndTramiteInterno() ? DominioSimNao.S.getDescricao() : DominioSimNao.N.getDescricao(), texto);
		addText(DateUtil.obterDataFormatada(programacao.getDtEntrega(),DD_MM_YYYY), texto);
		addText(DateUtil.obterDataFormatada(programacao.getEnvioAFP(),DD_MM_YYYY), texto);
		addText(programacao.getQtdItemAF(), texto);
		addText(programacao.getQtdTotalProg(), texto);
		addText(programacao.getQtdEntParc(), texto);
		addText(programacao.getDescCCSol(), texto);
		addText(programacao.getNroSol(), texto);
		addText(DateUtil.obterDataFormatada(programacao.getAssinatura(),DD_MM_YYYY), texto);
		addText(programacao.getJustEmpenho(), texto);
		addText(programacao.getQtdRecAF(), texto);
		addText(programacao.getTotalEntregue(), texto);
		 
		Double valorEfetivado = programacao.getValorEfetivado();
		addText((valorEfetivado!=null)?NumberFormat.getInstance(new Locale("pt","BR")).format (valorEfetivado).toString():"", texto);
		addText(programacao.getDescCCApp(), texto);
		addText(programacao.getObs(), texto);

		return texto.toString();
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return this.autFornecimentoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}
	
}

	