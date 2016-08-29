package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioAgendamentoConsultaController extends ActionReport {


	private static final Log LOG = LogFactory.getLog(RelatorioAgendamentoConsultaController.class);

	private static final long serialVersionUID = 7389612960603968696L;

	@Inject
	private HostRemotoCache hostRemoto;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer consultaNumero;
	
	private String labelZona;
	private String labelSala;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;
	
	private List<RelatorioAgendamentoConsultaVO> colecao = new LinkedList<RelatorioAgendamentoConsultaVO>();
	
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioAgendamentoConsulta.jasper";
	}

	@Override
	public Collection<RelatorioAgendamentoConsultaVO> recuperarColecao() throws ApplicationBusinessException {
		this.colecao = ambulatorioFacade.obterAgendamentoConsulta(this.consultaNumero);

		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		obterParametros();
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		params.put("dataAtual", sdf.format(dataAtual));
		params.put("labelZona", this.labelZona + ":");
		params.put("labelSala", this.labelSala + ":");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal();
		params.put("hospitalLocal", hospital);
		if (isGradeUBS()){
			String tituloUbs = null;
			try{
				tituloUbs = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_TITULO_UBS);
			}catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			if (StringUtils.isNotBlank(tituloUbs)){
				params.put("hospitalLocal", tituloUbs);
			}
		}
		return params;
	}

	public void imprimirAgendamentoConsulta(Integer consultaNumero) {
		this.consultaNumero = consultaNumero;
		this.obterParametros();
		directPrint();
	}

	private boolean isGradeUBS() {
		List<AacGradeAgendamenConsultas> listaGrade = ambulatorioFacade.listarGradeUnidFuncionalECaracteristicas(consultaNumero);
		AacGradeAgendamenConsultas grade = listaGrade.get(0);
		
		return grade.getUnidadeFuncional().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UBS);
	}

	/**
	 * Retorna impressora matricial do host remoto se houver.
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	private String getMatricialHost() throws UnknownHostException {
		String remoteHost = hostRemoto.getEnderecoRedeHostRemoto();
		ImpComputador computador = cadastrosBasicosCupsFacade
				.obterComputador(remoteHost);
		ImpComputadorImpressora impressora = null;
		if (computador != null) {
			impressora = cadastrosBasicosCupsFacade.obterImpressora(
					computador.getSeq(), DominioTipoCups.RAW);
		}

		return impressora == null ? null : impressora.getImpImpressora()
				.getFilaImpressora();
	}
	
	/**
	 * Metodo responsavel por imprimir via CUPS.
	 * Qualquer alteração neste método, deverá ser alterado também na classe ListarConsultasPorGradeController
	 */
	public void directPrint() {

		try {
			// prepara dados para geração do relatório.
			recuperarColecao();

			String matricial = this.getMatricialHost();

			if (StringUtils.isNotBlank(matricial)) {
				String textoMatricial = obterTextoAgendamentoConsulta();
				textoMatricial = Normalizer.normalize(textoMatricial,
						Normalizer.Form.NFD);
				textoMatricial = textoMatricial.replaceAll("[^\\p{ASCII}]", "");
				// Necessário adicionar algumas linhas no final para ficar no
				// ponto de corte correto da impressora.
				textoMatricial = textoMatricial.concat("\n\n\n\n\n\n");
				this.sistemaImpressao.imprimir(textoMatricial, matricial);

				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO_TICKET");

			} else {
				DocumentoJasper documento = gerarDocumento();

				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						hostRemoto.getEnderecoIPv4HostRemoto());

				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String obterTextoAgendamentoConsulta() throws ApplicationBusinessException {
		String hospitalLocal = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal();
		obterParametros();
		AacConsultas consulta = ambulatorioFacade.obterConsultaPorNumero(consultaNumero);
		return ambulatorioFacade.obterTextoAgendamentoConsulta(hospitalLocal, this.labelZona, this.labelSala, consulta);
	}

	
	/**
	 * Obtem os parametros zona e sala que serao exibidos no relatorio. 
	 */
	private void obterParametros() {
		try {
			this.labelZona = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			this.labelSala = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_SALA);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
}
