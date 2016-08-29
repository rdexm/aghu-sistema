package br.gov.mec.aghu.prescricaomedica.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioSumarioAltaController.Subtitulo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioAltaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioSumarioObitoVO;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller da aba de <b>Sumario</b> da tela de Relatorio de Conclusao do
 * Sumario Alta.<br>
 * 
 * @author rcorvalao
 */

public class RelatorioConclusaoAbaSumario extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6846835188454604920L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final Log LOG = LogFactory.getLog(RelatorioConclusaoAbaSumario.class);
	
	private List<MpmAltaSumario> listaAltasSumarios;
	private Integer seqAtendimento;
	private String subtitulo;
	private boolean previa;
	private String prontuarioMae = null;
	private boolean obito = false;
	private String voltarPara = "list";
	
	private AghAtendimentos atendimento;

    private Collection<?> listaPreviaAbaSumario;
	
	private MpmAltaSumario altaSumario;
	private String altaTipoOrigem;
	
	private String  tipoImpressao;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	//TODO:
	//@Observer("sumarioAltaConcluido")
	public void observarEventoImpressaoSumarioAlta(MpmAltaSumario pAltaSumario,
			String pAltaTipoOrigem, RapServidores servidorLogado) throws BaseException, JRException, SystemException,
			IOException {
		
		this.setObito(false);

		/**
		 * Lógica migrada na classe
		 * <code>RelatorioConclusaoSumarioAltaController</code>.
		 */
		this.renderAba(pAltaSumario, pAltaTipoOrigem);

		try {
			DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}

    private void populaDadosRelatorioAbaSumario() throws BaseException {
        if(isObito()){
            listaPreviaAbaSumario =  recuperarColecaoSumarioObito();
        }else {
            listaPreviaAbaSumario =  recuperarColecaoSumarioAlta();
        }
    }

	//TODO:
	//@Observer("sumarioObitoConcluido")
	public void observarEventoImpressaoSumarioObito(MpmAltaSumario pAltaSumario,
			String pAltaTipoOrigem, String tipoImpressao) throws BaseException, JRException, SystemException,
			IOException {

		this.setObito(true);

		/**
		 * Lógica migrada na classe
		 * <code>RelatorioConclusaoSumarioAltaController</code>.
		 */
		this.setTipoImpressao(tipoImpressao);
		this.renderAba(pAltaSumario, pAltaTipoOrigem);
		
		// Gera o PDF
		//super.print(false, isObito());

		try {
//			cupsFacade.enviarPdfCupsPorClasse(AghuParametrosEnum.P_CUPS_CLASSE_A_PDF,
//					getArquivoGerado(), DominioNomeRelatorio.SUMARIO_OBITO);
			
			DocumentoJasper documento = gerarDocumento();
			
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		}  catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(e.getLocalizedMessage());
		}
	}

	/**
	 * Metodo responsavel por inicializar as variaveis e fazer load do
	 * Relatorio.<br>
	 * Chamado toda vez que um click for efetudado na aba.<br>
	 * 
	 * @param altaSumario
	 * @param altaTipoOrigem
	 * @throws BaseException 
	 */
	public void renderAba(MpmAltaSumario altaSumarioAtual, String pAltaTipoOrigem) throws BaseException {
		if (this.altaSumario == null || (altaSumarioAtual != null && !this.altaSumario.getId().equals(altaSumarioAtual.getId()))) {
			this.altaSumario = altaSumarioAtual;
			this.setAltaTipoOrigem(pAltaTipoOrigem);
			this.loadRelatorio();
		}

        populaDadosRelatorioAbaSumario();
	}

	/**
	 * Metodo responsavel por fazer load do Relatorio.<br>
	 * O metodo de render da Aba deve controlar para chamar apenas uma vez.<br>
	 * 
	 */
	protected void loadRelatorio() {
		seqAtendimento = this.altaSumario.getId().getApaAtdSeq();
		previa = false;
		subtitulo = null;
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
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		if (this.altaSumario.getTipo().equals(DominioIndTipoAltaSumarios.OBT)) {
			setObito(true);
			setTipoImpressao("N");
		}

		DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection recuperarColecao() {
		return listaPreviaAbaSumario;
	}
	
	public Collection<RelSumarioAltaVO> recuperarColecaoSumarioAlta() throws BaseException {
		List<RelSumarioAltaVO> colecao = new ArrayList<RelSumarioAltaVO>(0);

		this.atendimento = this.aghuFacade.obterAtendimentoComPaciente(seqAtendimento);
		listaAltasSumarios = atendimento.getAltasSumario();
		if (listaAltasSumarios == null || listaAltasSumarios.isEmpty()) {
			return null;
		}

		// paciente recém nacido requer prontuário da mãe no relatório
		if (atendimento.getPaciente().getProntuario() != null
				&& atendimento.getPaciente().getProntuario() > VALOR_MAXIMO_PRONTUARIO &&
				atendimento.getPaciente().getMaePaciente() != null) {

			//if (atendimento.getPaciente().getMaePaciente() != null) {
				this.prontuarioMae = CoreUtil.formataProntuarioRelatorio(atendimento.getPaciente()
						.getProntuario())
						+ "          Mãe: "
						+ CoreUtil.formataProntuarioRelatorio(atendimento.getPaciente()
								.getMaePaciente().getProntuario());
			//}

		}

		MpmAltaSumarioId id = this.listaAltasSumarios.get(this.listaAltasSumarios.size() - 1).getId();

		colecao.add(this.buscaColecao(id));
		
		if (!colecao.isEmpty() && !this.previa){
			RelSumarioAltaVO voPai = colecao.get(0);
			int nroVias = this.obterNumeroViasRelatorioSumarioAlta();
			if (nroVias > 1) {
				this.prepararImprimirNovasVias(voPai, colecao, nroVias);
			}
		}
		
		return colecao;
	}
	
	private int obterNumeroViasRelatorioSumarioAlta() throws BaseException {
			final AghParametros paramViasSumarioAlta = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_NRO_VIAS_RELATORIO_SUMARIO_ALTA);
			return paramViasSumarioAlta.getVlrNumerico().intValue();

		/*int numeroVias;
		
		DominioOrigemAtendimento origemAtendimento = atendimento.getOrigem();
		DominioIndTipoAltaSumarios tipoAltaSumario = altaSumario.getTipo();
		
		if(DominioOrigemAtendimento.I.equals(origemAtendimento) && tipoAltaSumario == DominioIndTipoAltaSumarios.ALT) {
			numeroVias = 2;
		} else {
			numeroVias = 1;
		}
		
		return numeroVias;*/
	}

	public Collection<RelatorioSumarioObitoVO> recuperarColecaoSumarioObito() {
		this.atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(seqAtendimento);

		RelatorioSumarioObitoVO sumarioObito = null;
		try {
			sumarioObito = this.prescricaoMedicaFacade.criaRelatorioSumarioAlta(altaSumario.getId(), this.getTipoImpressao());
		} catch (BaseException e1) {
			LOG.error(e1.getMessage(),e1);
		}
		List<RelatorioSumarioObitoVO> listaSumarios = new ArrayList<RelatorioSumarioObitoVO>();
		listaSumarios.add(sumarioObito);
		
		/*-------------VERIFICA SE DEVE IMPRIMIR NOVAS VIAS--------------- */
		try{
			if (!listaSumarios.isEmpty() && !this.previa){
				RelatorioSumarioObitoVO voPai = listaSumarios.get(0);
				Integer nroVias = this.obterNumeroViasRelatorioSumarioObito();
				if (nroVias > 1) {
					this.prepararImprimirNovasViasObito(voPai, listaSumarios, nroVias);
				}
			}
		} catch(BaseException e){
			//Caso não exista o parâmetro das vias imprime apenas uma via
			LOG.error("Erro ao verificar se deve imprimir novas vias.", e);
		}
		/*------------------------------------------- */
		
		return listaSumarios;
	}
	
	/**
	 * Prepara novas vias para serem impressas
	 * 
	 * @param voPai
	 * @param colVOPai
	 * @param internacao
	 */
	protected void prepararImprimirNovasViasAlta(
			RelSumarioAltaVO voPai,
			Collection<RelSumarioAltaVO> colVOPai,
			Byte nroViasPme) {

		voPai.setOrdemTela(1);
		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			RelSumarioAltaVO voPaiNovaVia = voPai.copiar();
			voPaiNovaVia.setOrdemTela(ordemTela);
			colVOPai.add(voPaiNovaVia);
			ordemTela++;
		}
	}

	/**
	 * Prepara novas vias para serem impressas
	 * 
	 * @param voPai
	 * @param colVOPai
	 * @param internacao
	 */
	protected void prepararImprimirNovasVias(
			RelSumarioAltaVO voPai,
			Collection<RelSumarioAltaVO> colVOPai,
			Integer nroVias) {

		voPai.setOrdemTela(1);
		Integer ordemTela = 2;
		for (int i = 0; i < nroVias - 1; i++) {
			RelSumarioAltaVO voPaiNovaVia = voPai.copiar();
			if (voPaiNovaVia != null) {
			    voPaiNovaVia.setOrdemTela(ordemTela);
			    colVOPai.add(voPaiNovaVia);
			}
			ordemTela++;
		}
	}
	
	/**
	 * Prepara novas vias para serem impressas
	 * 
	 * @param voPai
	 * @param colVOPai
	 * @param nroVias
	 */
	protected void prepararImprimirNovasViasObito(
			RelatorioSumarioObitoVO voPai,
			Collection<RelatorioSumarioObitoVO> colVOPai,
			Integer nroVias) {

		voPai.setOrdemTela(1);
		Integer ordemTela = 2;
		for (int i = 0; i < nroVias - 1; i++) {
			RelatorioSumarioObitoVO voPaiNovaVia = voPai.copiar();
			voPaiNovaVia.setOrdemTela(ordemTela);
			colVOPai.add(voPaiNovaVia);
			ordemTela++;
		}
	}

	private Integer obterNumeroViasRelatorioSumarioObito()
			throws BaseException {
		final AghParametros paramViasSumarioObito = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_NRO_VIAS_RELATORIO_SUMARIO_OBITO);
		return paramViasSumarioObito.getVlrNumerico().intValue();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		if (isObito()) {
			return "br/gov/mec/aghu/prescricaomedica/report/relatorioSumarioObito.jasper";
		} else {
			return "br/gov/mec/aghu/prescricaomedica/report/relatorioSumarioAlta.jasper";
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		if (!isObito()) {

			params.put("subtitulo", (subtitulo != null ? Subtitulo.valueOf(subtitulo).getTexto()
					: ""));
			params.put("previa", previa);
			params.put("dataAtual", new Date());
			params.put("prontuarioMae", this.prontuarioMae);
		}			
		
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");
		params.put("previaUrlImagem", recuperaCaminhoImgBackground());
		
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo2());
			params.put("localizadorInternet", examesFacade.buscarLocalizadorExamesInternet(seqAtendimento));
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}
		
		return params;
	}

	@Override
	protected BaseEntity getEntidadePai() {
		return atendimento;
	}

	public String cancelar() {
		if (!StringUtils.isEmpty(voltarPara)) {
			return voltarPara;
		} else {
			return "list";
		}
	}

	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}

	private RelSumarioAltaVO buscaColecao(final MpmAltaSumarioId id) {
		//TODO:
		return prescricaoMedicaFacade.criaRelatorioSumarioAltaPorId(id);
	}
	public String getTipoImpressao() {
		return tipoImpressao;
	}
	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}
	public boolean isObito() {
		return obito;
	}
	public void setObito(boolean obito) {
		this.obito = obito;
	}
	public String getAltaTipoOrigem() {
		return altaTipoOrigem;
	}
	public void setAltaTipoOrigem(String altaTipoOrigem) {
		this.altaTipoOrigem = altaTipoOrigem;
	}

}
