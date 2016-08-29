package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.prescricaomedica.vo.FormularioDispAntimicrobianosVO;

import com.itextpdf.text.DocumentException;

public class FormularioDispAntimicrobianosController extends
ActionReport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8715126248062313120L;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<FormularioDispAntimicrobianosVO> colecao = new ArrayList<FormularioDispAntimicrobianosVO>(0);

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

//	@Observer("prescricaoConfirmada") 
	public void observarEventoImpressaoPrescricaoConfirmada() throws BaseException, JRException,
			SystemException, IOException {

		/*init();

		if (colecao != null && !colecao.isEmpty()) {

			// Gera o PDF
			print(false);
			
			//Busca impressora padrão da unidade funcional do paciente (atendimento)
			ImpImpressora impressoraUnidadeFuncional = null;
			try {
				if (prescricaoMedicaVO.getPrescricaoMedica() != null 
						&& prescricaoMedicaVO.getPrescricaoMedica().getAtendimento() != null 
						&& prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getUnidadeFuncional() != null) {
					AghImpressoraPadraoUnids aghImpPadraoUnid = aghuFacade.obterImpressora(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getSeq(), TipoDocumentoImpressao.FARMACIA_DISP);
					impressoraUnidadeFuncional = aghImpPadraoUnid != null ? aghImpPadraoUnid.getImpImpressora() : null;
				}
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				impressoraUnidadeFuncional = null;
			}

			try {
				
				if(impressoraUnidadeFuncional != null) {
					cupsFacade.enviarPdfCupsPorImpressora(impressoraUnidadeFuncional, getArquivoGerado(), DominioNomeRelatorio.DISPENSACAO_FARMARCIA);
				} else {
					// Obtendo as impressoras cadastradas para impressão e enviando
					// para impressão.
					HashSet<ImpImpressora> impressoras = new HashSet<ImpImpressora>();

					for (DispensacaoFarmaciaVO item : colecao) {
						for (ImpImpressora impressora : item.getImpressorasCups()) {
							impressoras.add(impressora);
						}
					}
		
//					for (ImpImpressora imp : impressoras) {
//						cupsFacade.enviarPdfCupsPorImpressora(imp, getArquivoGerado(), 
//							DominioNomeRelatorio.DISPENSACAO_FARMARCIA);
//					}
					
					for (ImpImpressora imp : impressoras) {
						cupsFacade.enviarPdfCupsPorImpressora(imp, getArquivoGerado(), 
							DominioNomeRelatorio.DISPENSACAO_FARMARCIA);
					}
				}
				
				this.getStatusMessages().addFromResourceBundle(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Farmácia");

			} catch (AGHUNegocioException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				StatusMessages.instance().add(Severity.ERROR, e.getLocalizedMessage());
			}
		}*/

	}

	public void init() {
		FormularioDispAntimicrobianosVO formulario = (FormularioDispAntimicrobianosVO) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getAttribute("formulario");
		this.colecao.clear();
		if(formulario != null){
			this.colecao.add(formulario);
			((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().removeAttribute("formulario");
		}

	}


	/**
	 * Prepara novas vias para serem impressas
	 * 
	 * @param nroViasPme
	 */
	protected void prepararImprimirNovasVias(Byte nroViasPme) {
		/*List<DispensacaoFarmaciaVO> listaNovasVias = new ArrayList<DispensacaoFarmaciaVO>();
		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			for (DispensacaoFarmaciaVO dispensacao : listaDispensacaoFarmaciaVO) {
				dispensacao.setOrdemTela(1);
				DispensacaoFarmaciaVO dispNovaVia = dispensacao.copiar();
				dispNovaVia.setOrdemTela(ordemTela);
				listaNovasVias.add(dispNovaVia);
			}
			for (DispensacaoFarmaciaVO dispensacao : listaMedicamentosMovimentados) {
				dispensacao.setOrdemTela(1);
				DispensacaoFarmaciaVO dispNovaVia = dispensacao.copiar();
				dispNovaVia.setOrdemTela(ordemTela);
				listaNovasVias.add(dispNovaVia);
			}
			ordemTela++;
		}
		if (listaDispensacaoFarmaciaVO.size() > 0) {
			listaDispensacaoFarmaciaVO.addAll(listaNovasVias);

		} else {
			listaMedicamentosMovimentados.addAll(listaNovasVias);
		}*/
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/formularioDispAntimicrobiano.jasper";
	}

	@Override
	public Collection<FormularioDispAntimicrobianosVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
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
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String caminhoSubRelatorio = request.getRealPath("WEB-INF/classes/br/gov/mec/aghu/prescricaomedica/report/");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SUBREPORT_DIR_LISTA", caminhoSubRelatorio + "/formularioDispAntimicrobiano_lista.jasper");
		params.put("SUBREPORT_DIR_CCIH", caminhoSubRelatorio + "/formularioDispAntimicrobiano_ccih.jasper");
		params.put("SUBREPORT_DIR_FARMACIA", caminhoSubRelatorio + "/formularioDispAntimicrobiano_farmacia.jasper");
		
		return params;
	}

	public List<FormularioDispAntimicrobianosVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<FormularioDispAntimicrobianosVO> colecao) {
		this.colecao = colecao;
	}
	
	
}
