package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSolHemoterapicaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author ehgsilva
 */

public class RelatorioSolHemoterapicaController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3689622479805747029L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	//@Out
	private Boolean imprimirRelatorioHemoterapica = true;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	private PrescricaoMedicaVO prescricaoMedicaVO;

	@EJB
	private IParametroFacade parametroFacade;

	private EnumTipoImpressao tipoImpressao;
	
	private RapServidores servidorValida;
	
	private Date dataMovimento;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelSolHemoterapicaVO> colecao = new ArrayList<RelSolHemoterapicaVO>(0);
	
	private static final Log LOG = LogFactory.getLog(RelatorioSolHemoterapicaController.class);

	//@Observer("prescricaoConfirmada")
	public void observarEventoImpressaoPrescricaoConfirmada() throws JRException,
			SystemException, IOException {

		print();

		if (getImprimir()) {
			
			try {
				AghParametros paramUnidadeBancoSangue = parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_UNF_BANCO_SANGUE);
				AghUnidadesFuncionais unidade = new AghUnidadesFuncionais(
						paramUnidadeBancoSangue.getVlrNumerico().shortValue());

				this.sistemaImpressao.imprimir(gerarDocumento()
						.getJasperPrint(), unidade,
						TipoDocumentoImpressao.PRESCRICAO_MEDICA);

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Hemoterapia");
				
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");				
			}

		}

	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioSolHemoterapicas.jasper";
	}

	@Override
	public Collection<RelSolHemoterapicaVO> recuperarColecao()  {

		return this.colecao;
	}

	public void print() {
		try {
		
			MpmPrescricaoMedica prescricaoMedica = prescricaoMedicaVO.getPrescricaoMedica();
			List<RelSolHemoterapicaVO> listaHemoterapiasVO = prescricaoMedicaFacade
					.pesquisarHemoterapiasRelatorio(prescricaoMedica, tipoImpressao, servidorValida, dataMovimento);
			colecao = new ArrayList<RelSolHemoterapicaVO>();
			colecao.addAll(listaHemoterapiasVO);
			if (colecao.isEmpty()) {
				this.imprimirRelatorioHemoterapica = false;
			}

		} catch (BaseException e) {
			this.imprimirRelatorioHemoterapica = false;
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");
		if (this.colecao.size() > 0) {
			RelSolHemoterapicaVO relSolHemoterapicaVO = (RelSolHemoterapicaVO) this.colecao.get(0);
			String prontuarioNome = relSolHemoterapicaVO.getPacienteProntuario() + " - "
					+ relSolHemoterapicaVO.getPacienteNome();
			params.put("pacienteProntuarioNome", prontuarioNome);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat dfApenasData = new SimpleDateFormat("dd/MM/yyyy");

			String sDataNascimento = dfApenasData.format(relSolHemoterapicaVO.getDataNascimento());
			String sDataHora = df.format(relSolHemoterapicaVO.getDataHora());
			params.put("dtNascimento", sDataNascimento);
			params.put("dtHora", sDataHora);

			String dataAtual = df.format(new Date());
			params.put("dataAtual", dataAtual);
		}
		return params;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException,
			JRException, SystemException, DocumentException {
		DocumentoJasper documento= gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));

	}

	/*
	 * GETs e SETs
	 */
	public Boolean getImprimir() {
		return imprimirRelatorioHemoterapica;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimirRelatorioHemoterapica = imprimir;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public List<RelSolHemoterapicaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelSolHemoterapicaVO> colecao) {
		this.colecao = colecao;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public RapServidores getServidorValida() {
		return servidorValida;
	}

	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}
	
	
}
