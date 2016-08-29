package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAtestadoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class RelatorioAtestadoAcompanhanteController extends ActionReport {


	private static final long serialVersionUID = 2921505139716177722L;
	private static final Log LOG = LogFactory.getLog(RelatorioAtestadoAcompanhanteController.class);
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	private List<MamAtestados> consultaAtestado = new ArrayList<MamAtestados>();
	List<RelatorioAtestadoVO> listaAtestadoVO = new ArrayList<RelatorioAtestadoVO>();

	private MamAtestados atestadoAmbulatorio = new MamAtestados();
	private List<MamTipoAtestado> tipoAtestados =  new ArrayList<MamTipoAtestado>();
	private MamTipoAtestado atestadoAcompanhante = new MamTipoAtestado();
	
	public void recuperaTipoAteChavePrimaria() {
		tipoAtestados = ambulatorioFacade.listarTodos();
		for (MamTipoAtestado tipoAcompanhante : tipoAtestados) {
			if (tipoAcompanhante.getDescricao().equalsIgnoreCase("Atestado de Acompanhamento")) {
				atestadoAcompanhante = tipoAcompanhante;
			}
		}
	}

	public void recuperarAtestado(AacConsultas consNumero, Long idAtestado) {
		recuperaTipoAteChavePrimaria();
		atestadoAmbulatorio.setConsulta(consNumero);
		atestadoAmbulatorio.setSeq(idAtestado);
		atestadoAmbulatorio.setMamTipoAtestado(atestadoAcompanhante);
		consultaAtestado = ambulatorioFacade.listarAtestado(atestadoAmbulatorio);
	}

	public Collection<RelatorioAtestadoVO> recuperarColecao() throws ApplicationBusinessException {
		listaAtestadoVO = ambulatorioFacade.recuperarInfConsultaAtestados(consultaAtestado);
		return listaAtestadoVO;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/atestadoAcompanhante.jasper";
	}
	
	public void directPrint() throws ApplicationBusinessException {

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO", new Object[0]);

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO", new Object[0]);
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeHospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal());
		try {
			params.put("footerCaminhoLogo", recuperarCaminhoLogo2());
			params.put("nomeCidade", parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE).getVlrTexto());
			params.put("nomeUf", parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU).getVlrTexto());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		return params;
	}

}
