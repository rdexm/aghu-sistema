package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ReceitaMedicaVO;

import com.itextpdf.text.DocumentException;

public class RelatorioReceitaMedicaController extends ActionReport {

	private static final long serialVersionUID = 4385182640636366902L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private List<ReceitaMedicaVO> colecao = new ArrayList<ReceitaMedicaVO>(0);

	private static final Log LOG = LogFactory.getLog(RelatorioReceitaMedicaController.class);

	// parâmetros - chave composta de sumário de alta
	private Integer apaAtdSeq;
	private Integer apaSeq;
	private Short seqp;
	
	// Obter Receituario Por Numero Receita
	private Long receitaSeq;
	private Boolean imprimiu;

	private String voltarPara;

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/ReceitaMedica.jasper";
	}

	@Override
	public Collection<ReceitaMedicaVO> recuperarColecao() {
		return this.colecao;
	}

	/**
	 * Busca os dados para o relatório e redireciona para a página com o relatório embutido.<br>
	 * Usado quando vem de formulario.
	 */
	public String busca() {
		try {
			MpmAltaSumarioId id = new MpmAltaSumarioId(this.apaAtdSeq, this.apaSeq, this.seqp);
			MpmAltaSumario altaSumario = new MpmAltaSumario(id);
			colecao = ambulatorioFacade.imprimirReceita(altaSumario, false);
		} catch (BaseException  e) {
			apresentarExcecaoNegocio(e);
		}
		return "receitaMedicaPdf";
	}

	/**
	 * Busca os dados para o relatório e monta a página com o relatório. <br>
	 * Usado quando vem por link e parâmetros.
	 */
	public String print() {
		this.busca();
		return null;
	}
	
	public String pdfByLink(){
		return "receitaMedicaPdf";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");
		try {
			params.put("LOGO_PATH", recuperarCaminhoLogo2());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}
		
		String enderecoCompleto = getEnderecoCompleto();
		if (!enderecoCompleto.isEmpty()) {
			params.put("ENDERECO_COMPLETO", enderecoCompleto);
		}
		
		params.put("nomeHospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal());
		
		return params;
	}	
	
	private String getEnderecoCompleto() {
		StringBuilder enderecoCompleto = new StringBuilder("");
		
		AghParametros logradouro;
		AghParametros cep;
		AghParametros telefone;
		AghParametros cidade;
		AghParametros uf;
		
		try {
			logradouro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO);
			if (logradouro != null && logradouro.getVlrTexto() != null) {
				enderecoCompleto.append(logradouro.getVlrTexto()).append('\n');
			}
			
			cep = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CEP);
			if (cep != null && cep.getVlrTexto() != null) {
				enderecoCompleto.append("CEP ").append(cep.getVlrTexto());
			}
			
			telefone = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_FONE);
			if (telefone != null && telefone.getVlrTexto() != null) {
				enderecoCompleto.append(" Fone: ").append(telefone.getVlrTexto());
			}
			
			cidade = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
			if (cidade != null && cidade.getVlrTexto() != null) {
				enderecoCompleto.append(" - ").append(cidade.getVlrTexto());
			}

			uf = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
			if (uf != null && uf.getVlrTexto() != null) {
				enderecoCompleto.append('/').append(uf.getVlrTexto());
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return enderecoCompleto.toString();
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException,
		ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));

	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() {
		try {
			colecao = ambulatorioFacade.imprimirReceitaPorSeq(receitaSeq, imprimiu);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	
	public String voltar(){
		return voltarPara;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public Integer getApaAtdSeq() {
		return apaAtdSeq;
	}

	public void setApaAtdSeq(Integer apaAtdSeq) {
		this.apaAtdSeq = apaAtdSeq;
	}

	public Integer getApaSeq() {
		return apaSeq;
	}

	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Long getReceitaSeq() {
		return receitaSeq;
	}

	public void setReceitaSeq(Long receitaSeq) {
		this.receitaSeq = receitaSeq;
	}

	public void setImprimiu(Boolean imprimiu) {
		this.imprimiu = imprimiu;
	}

	public Boolean getImprimiu() {
		return imprimiu;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}