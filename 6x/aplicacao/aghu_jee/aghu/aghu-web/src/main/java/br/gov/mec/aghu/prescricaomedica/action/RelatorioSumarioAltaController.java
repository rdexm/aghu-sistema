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
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioAltaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioSumarioObitoVO;
import net.sf.jasperreports.engine.JRException;

/**
 * @author tfelini
 */
@SelectionQualifier
public class RelatorioSumarioAltaController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7282636945582135015L;

	

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao; 

	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAltaController.class);
	
	private static final String PAGE_RETORNO_PADRAO = "list";
	
	private List<MpmAltaSumario> listaAltasSumarios;
	private Integer seqAtendimento;
	private String subtitulo;
	private boolean previa;
	private String prontuarioMae = null;
	private boolean obito = false;
	private String voltarPara = PAGE_RETORNO_PADRAO;
	
	private AghAtendimentos atendimento;
	
	private MpmAltaSumario altaSumario;
	private String altaTipoOrigem;
	
	private String  tipoImpressao;
	
	private Boolean imprimimindoRelSumario = Boolean.FALSE;
	private Boolean imprimeVias = Boolean.FALSE;
    private Collection<?> listaRelatorio;
    
    private final String PAGE_LANCAR_ITENS_CONTA_HOSPITALAR_LIST = "faturamento-lancarItensContaHospitalarList";
    
    private Boolean exibeBotaoVoltar = true;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		// tem que deixar por enquanto este metodo aqui pois
		// senao nao resolve o EJB da ON
		this.buscaColecao(null);
		
		this.carregarParametros();	
	}
	
	private void carregarParametros() {
		String voltarPara = this.getRequestParameter("voltarPara");
		if(StringUtils.isNotBlank(voltarPara) && voltarPara.equalsIgnoreCase(PAGE_LANCAR_ITENS_CONTA_HOSPITALAR_LIST)) {
			this.setVoltarPara(voltarPara);
			String atdSeq = this.getRequestParameter("atdSeq");
			if(StringUtils.isNotBlank(atdSeq)) {
				this.setSeqAtendimento(Integer.valueOf(atdSeq));
			}
			String subtitulo = this.getRequestParameter("subtitulo");
			if(StringUtils.isNotBlank(subtitulo)) {
				this.setSubtitulo(subtitulo);
			}
			String previa = this.getRequestParameter("previa");
			if(StringUtils.isNotBlank(previa)) {
				this.setPrevia(Boolean.valueOf(previa));
			}
			
			this.populaRelatorioAltaObito();
			this.exibeBotaoVoltar = false;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection recuperarColecao() {
        return listaRelatorio;
	}

    public void populaRelatorioAltaObito() {
        if(isObito()){
            listaRelatorio =  this.recuperarColecaoSumarioObito();
        }else {
            listaRelatorio = this.recuperarColecaoSumarioAlta();
        }
    }
		
	public Collection<RelSumarioAltaVO> recuperarColecaoSumarioAlta() {
		List<RelSumarioAltaVO> colecao = new ArrayList<RelSumarioAltaVO>(0);

		this.setAtendimento(this.aghuFacade.obterAtendimentoComPaciente(seqAtendimento));
		
		if(atendimento != null) {
			listaAltasSumarios = this.prescricaoMedicaFacade.pesquisarAltaSumarios(atendimento.getSeq());
		}
		
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

		MpmAltaSumarioId id = this.listaAltasSumarios.get(0).getId();

		if (id != null) {
			LOG.warn("ID nao esta nulo...");
			colecao.add(this.buscaColecao(id));
		} else {
			LOG.warn("ID esta nulo...");
		}
		
		/*-------------VERIFICA SE DEVE IMPRIMIR NOVAS VIAS--------------- */
		try{
			if (!colecao.isEmpty()
					&& !this.isPrevia()
					&& (!this.getImprimimindoRelSumario() || (this
							.getImprimimindoRelSumario() && this
							.getImprimeVias()))) {
				RelSumarioAltaVO voPai = colecao.get(0);
				Integer nroVias = this.obterNumeroViasRelatorioSumarioAlta();
				if (nroVias > 1) {
					this.prepararImprimirNovasVias(voPai, colecao, nroVias);
				}
			}
		} catch(BaseException e){
			//Caso não exista o parâmetro das vias imprime apenas uma via
			LOG.error("Erro ao verificar se deve imprimir novas vias.", e);
		}
		/*------------------------------------------- */
		
		return colecao;
	}
	
	private Integer obterNumeroViasRelatorioSumarioAlta()
			throws BaseException {
		final AghParametros paramViasSumarioAlta = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_NRO_VIAS_RELATORIO_SUMARIO_ALTA);
		return paramViasSumarioAlta.getVlrNumerico().intValue();
	}

	public Collection<RelatorioSumarioObitoVO> recuperarColecaoSumarioObito() {
		this.setAtendimento(this.aghuFacade.obterAghAtendimentoPorChavePrimaria(seqAtendimento));

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
			if (!listaSumarios.isEmpty()
					&& !this.isPrevia()
					&& (!this.getImprimimindoRelSumario() || (this
							.getImprimimindoRelSumario() && this
							.getImprimeVias()))) {
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

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException, DocumentException {
		
		this.setImprimeVias(Boolean.FALSE);
		this.setImprimimindoRelSumario(Boolean.TRUE);
		
		DocumentoJasper documento = gerarDocumento(DominioTipoDocumento.SA, Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {
		this.setImprimimindoRelSumario(true);
		this.setImprimeVias(true);
		try {
			DocumentoJasper documento = gerarDocumento(DominioTipoDocumento.SA, Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		this.setImprimeVias(false);
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
			return PAGE_RETORNO_PADRAO;
		}
	}

	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}

	private RelSumarioAltaVO buscaColecao(final MpmAltaSumarioId id) {
		return prescricaoMedicaFacade.criaRelatorioSumarioAltaPorId(id);
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
	
	protected void apresentarExcecaoNegocio(BaseException e) {
		// Apenas apresenta a mensagem de erro negocial para o cliente
		apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
	}
	
	/**
	 *  Limpa todos os parâmetros/atributos do relatório
	 */
	protected void limparParametros(){
		this.setListaAltasSumarios(null);
		this.setSeqAtendimento(null);
		this.setSubtitulo(null);
		this.setPrevia(false);
		this.setProntuarioMae(null);
		this.setObito(false);
		this.setVoltarPara(PAGE_RETORNO_PADRAO);
		this.setAtendimento(null);
		this.setAltaSumario(null);
		this.setTipoImpressao(null);
	}

	public List<MpmAltaSumario> getListaAltasSumarios() {
		return listaAltasSumarios;
	}
	
	public void setListaAltasSumarios(List<MpmAltaSumario> listaAltasSumarios) {
		this.listaAltasSumarios = listaAltasSumarios;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public String getSubtitulo() {
		return subtitulo;
	}

	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}

	public boolean isPrevia() {
		return previa;
	}

	public void setPrevia(boolean previa) {
		this.previa = previa;
	}

	public enum Subtitulo {
		REIMPRESSAO("** Reimpressão **"), PREVIA(
				"Este não é um documento definitivo. Não deve ser arquivado.");

		private String texto;

		private Subtitulo(String texto) {
			this.texto = texto;
		}

		public String getTexto() {
			return texto;
		}
	}

	public boolean isObito() {
		return obito;
	}

	public void setObito(boolean obito) {
		this.obito = obito;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	
	/**
	 * @param altaSumario
	 *            the altaSumario to set
	 */
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	/**
	 * @return the altaSumario
	 */
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	/**
	 * @param altaTipoOrigem
	 *            the altaTipoOrigem to set
	 */
	public void setAltaTipoOrigem(String altaTipoOrigem) {
		this.altaTipoOrigem = altaTipoOrigem;
	}

	/**
	 * @return the altaTipoOrigem
	 */
	public String getAltaTipoOrigem() {
		return altaTipoOrigem;
	}

	public String getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}
	
	public String getProntuarioMae() {
		return prontuarioMae;
	}
	
	public void setProntuarioMae(String prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Boolean getImprimimindoRelSumario() {
		return imprimimindoRelSumario;
	}

	public void setImprimimindoRelSumario(Boolean imprimimindoRelSumario) {
		this.imprimimindoRelSumario = imprimimindoRelSumario;
	}

	public Boolean getImprimeVias() {
		return imprimeVias;
	}

	public void setImprimeVias(Boolean imprimeVias) {
		this.imprimeVias = imprimeVias;
	}

	public Boolean getExibeBotaoVoltar() {
		return exibeBotaoVoltar;
	}

	public void setExibeBotaoVoltar(Boolean exibeBotaoVoltar) {
		this.exibeBotaoVoltar = exibeBotaoVoltar;
	}
	
	

}