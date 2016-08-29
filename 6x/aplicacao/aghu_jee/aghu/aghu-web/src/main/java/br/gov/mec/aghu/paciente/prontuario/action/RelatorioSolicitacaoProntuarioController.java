package br.gov.mec.aghu.paciente.prontuario.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.paciente.prontuario.vo.RelatorioSolicitacaoProntuarioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

@SessionScoped
@Named
public class RelatorioSolicitacaoProntuarioController extends ActionReport {

	private static final long serialVersionUID = -3723018300837588722L;
	private static final Log LOG = LogFactory.getLog(RelatorioSolicitacaoProntuarioController.class);
	private static final String REDIRECT_PESQUISAR_SOLICITACAO_PRONTUARIO = "solicitacaoProntuarioList";
	private static final String REDIRECT_MANTER_SOLICITACAO_PRONTUARIO = "solicitacaoProntuario";
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private List<AipPacientes> listaPacientes;

	private String solicitante;

	private String responsavel;

	private String observacao;

	private String especialidade;

	private Integer codigoSolicitacaoProntuario;

	private String totalProntuarios;

	private String cameFrom;

	private static final Comparator<AipPacientes> COMPARATOR_PACIENTE_SOLICITACAO_PRONTUARIO = new Comparator<AipPacientes>() {
		@Override
		public int compare(AipPacientes o1, AipPacientes o2) {

			// se o tamanho dos prontuários for maior que 3, comparar apenas os
			// últimos números
			if (o1.getProntuario().toString().length() > 3
					&& o2.getProntuario().toString().length() > 3) {

				String substringFinal1 = o1
						.getProntuario()
						.toString()
						.substring(o1.getProntuario().toString().length() - 3,
								o1.getProntuario().toString().length() - 1);

				String substringFinal2 = o2
						.getProntuario()
						.toString()
						.substring(o2.getProntuario().toString().length() - 3,
								o2.getProntuario().toString().length() - 1);

				Integer intInicioProntuario1 = o1.getProntuario() / 1000;
				Integer intInicioProntuario2 = o2.getProntuario() / 1000;

				int resultadoComparacao = substringFinal1.compareTo(substringFinal2);

				if (resultadoComparacao == 0) {
					resultadoComparacao = intInicioProntuario1.compareTo(intInicioProntuario2);
				}

				return resultadoComparacao;
			}

			// se um dos prontuários dor menor igual que 3 digitos, compara pelo
			// tamanho
			if (o1.getProntuario().toString().length() < o2.getProntuario().toString().length()) {
				return -1;
			} else if (o1.getProntuario().toString().length() > o2.getProntuario().toString()
					.length()) {
				return 1;
			}

			// se os tamanhos forem iguais e o tamanho do primeiro prontuário
			// for 3
			if (o1.getProntuario().toString().length() == o2.getProntuario().toString().length()
					&& o1.getProntuario().toString().length() == 3) {
				return o1
						.getProntuario()
						.toString()
						.substring(o1.getProntuario().toString().length() - 3,
								o1.getProntuario().toString().length() - 1)
						.compareTo(
								o2.getProntuario()
										.toString()
										.substring(o2.getProntuario().toString().length() - 3,
												o2.getProntuario().toString().length() - 1));
			}
			if (o1.getProntuario().toString().length() == o2.getProntuario().toString().length()
					&& o1.getProntuario().toString().length() == 2) {
				return o1
						.getProntuario()
						.toString()
						.substring(o1.getProntuario().toString().length() - 2,
								o1.getProntuario().toString().length() - 1)
						.compareTo(
								o2.getProntuario()
										.toString()
										.substring(o2.getProntuario().toString().length() - 2,
												o2.getProntuario().toString().length() - 1));
			}
			return 0;
		}

	};

	public String voltar(){
		if(cameFrom.equalsIgnoreCase("pesquisa")){
			return REDIRECT_PESQUISAR_SOLICITACAO_PRONTUARIO;
		}
		else if (cameFrom.equalsIgnoreCase("detalhe")){
			return REDIRECT_MANTER_SOLICITACAO_PRONTUARIO;
		}
		return null;
	}
	
	public String print(AipSolicitacaoProntuarios solicitacaoProntuario,
			List<AipPacientes> listaPacientes) throws  JRException,
			SystemException, IOException {
		if (solicitacaoProntuario.getSolicitante() != null) {
			this.solicitante = solicitacaoProntuario.getSolicitante();
		} else {
			this.solicitante = "";
		}
		if (solicitacaoProntuario.getResponsavel() != null) {
			this.responsavel = solicitacaoProntuario.getResponsavel();
		} else {
			this.responsavel = "";
		}
		if (solicitacaoProntuario.getObservacao() != null) {
			this.observacao = solicitacaoProntuario.getObservacao();
		} else {
			this.observacao = "";
		}
		if (solicitacaoProntuario.getAghEspecialidades() != null) {
			this.especialidade = solicitacaoProntuario.getAghEspecialidades()
					.getNomeEspecialidade();
		} else {
			this.especialidade = "";
		}
		this.codigoSolicitacaoProntuario = solicitacaoProntuario.getCodigo();

		Collections.sort(listaPacientes, COMPARATOR_PACIENTE_SOLICITACAO_PRONTUARIO);

		this.listaPacientes = listaPacientes;
		//return print(true);
		return null;
	}

	public void print(AipSolicitacaoProntuarios solicitacaoProntuario, String cameFrom) throws 
			JRException, SystemException, IOException {
		this.cameFrom = cameFrom;

		if (solicitacaoProntuario.getSolicitante() != null) {
			this.solicitante = solicitacaoProntuario.getSolicitante();
		} else {
			this.solicitante = "";
		}
		if (solicitacaoProntuario.getResponsavel() != null) {
			this.responsavel = solicitacaoProntuario.getResponsavel();
		} else {
			this.responsavel = "";
		}
		if (solicitacaoProntuario.getObservacao() != null) {
			this.observacao = solicitacaoProntuario.getObservacao();
		} else {
			this.observacao = "";
		}

		if (solicitacaoProntuario.getAghEspecialidades() != null) {
			this.especialidade = solicitacaoProntuario.getAghEspecialidades()
					.getNomeEspecialidade();
		} else {
			this.especialidade = "";
		}
		this.codigoSolicitacaoProntuario = solicitacaoProntuario.getCodigo();
		ArrayList<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		for (AipPacientes paciente : solicitacaoProntuario.getAipPacientes()) {
			pacientes.add(paciente);
		}

		Collections.sort(pacientes, COMPARATOR_PACIENTE_SOLICITACAO_PRONTUARIO);

		this.listaPacientes = pacientes;
		Integer size = listaPacientes.size();
		this.totalProntuarios = size.toString();
	}

	/**
	 * Impressao direta via CUPS.
	 * 
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void impressaoDireta(AipSolicitacaoProntuarios solicitacaoProntuario, String cameFrom)
			throws  JRException, SystemException, IOException, ParseException {

		this.cameFrom = cameFrom;

		if (solicitacaoProntuario.getSolicitante() != null) {
			this.solicitante = solicitacaoProntuario.getSolicitante();
		} else {
			this.solicitante = "";
		}
		if (solicitacaoProntuario.getResponsavel() != null) {
			this.responsavel = solicitacaoProntuario.getResponsavel();
		} else {
			this.responsavel = "";
		}
		if (solicitacaoProntuario.getObservacao() != null) {
			this.observacao = solicitacaoProntuario.getObservacao();
		} else {
			this.observacao = "";
		}

		if (solicitacaoProntuario.getAghEspecialidades() != null) {
			this.especialidade = solicitacaoProntuario.getAghEspecialidades()
					.getNomeEspecialidade();
		} else {
			this.especialidade = "";
		}
		this.codigoSolicitacaoProntuario = solicitacaoProntuario.getCodigo();
		ArrayList<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		for (AipPacientes paciente : solicitacaoProntuario.getAipPacientes()) {
			pacientes.add(paciente);
		}

		Collections.sort(pacientes, COMPARATOR_PACIENTE_SOLICITACAO_PRONTUARIO);

		this.listaPacientes = pacientes;
		Integer size = listaPacientes.size();
		this.totalProntuarios = size.toString();

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
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
	 * @throws MECBaseException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException, 
			JRException, SystemException, DocumentException, ApplicationBusinessException {

		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Collection<RelatorioSolicitacaoProntuarioVO> recuperarColecao() {
		List<RelatorioSolicitacaoProntuarioVO> lista = new ArrayList<RelatorioSolicitacaoProntuarioVO>();
		for (AipPacientes paciente : listaPacientes) {
			RelatorioSolicitacaoProntuarioVO relatorioSolicitacaoProntuarioVO = new RelatorioSolicitacaoProntuarioVO();
			String prontuario = paciente.getProntuario().toString();
			String prontuarioSemDigito = prontuario.substring(0, prontuario.length() - 1);
			prontuarioSemDigito = StringUtils.leftPad(prontuarioSemDigito, 8, '0');
			relatorioSolicitacaoProntuarioVO.setProntuarioSemDigito(prontuarioSemDigito);

			if (paciente.getIndPacienteVip().equals(DominioSimNao.S)) {
				relatorioSolicitacaoProntuarioVO.setIndicador("VIP");
			} else if (paciente.getPrntAtivo().equals(DominioTipoProntuario.P)) {
				relatorioSolicitacaoProntuarioVO.setIndicador("P");
			} else if (paciente.getDtObito() == null && paciente.getDtObitoExterno() == null) {
				relatorioSolicitacaoProntuarioVO.setIndicador("");
			} else {
				relatorioSolicitacaoProntuarioVO.setIndicador("O");
			}
			lista.add(relatorioSolicitacaoProntuarioVO);
		}
		return lista;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/prontuario/report/relatorioSolicitacaoProntuario.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeRelatorio", "AIPR_PRONT_PESQ_ORD");
		params.put("solicitante", this.solicitante);
		params.put("responsavel", this.responsavel);
		params.put("especialidade", this.especialidade);
		params.put("observacao", this.observacao);
		params.put("nomeHospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		params.put("totalProntuarios", this.totalProntuarios);
		return params;
	}

	// GETs AND SETs

	/**
	 * @return the listaPacientes
	 */
	public List<AipPacientes> getListaPacientes() {
		return listaPacientes;
	}

	/**
	 * @param listaPacientes
	 *            the listaPacientes to set
	 */
	public void setListaPacientes(List<AipPacientes> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	/**
	 * @return the solicitante
	 */
	public String getSolicitante() {
		return solicitante;
	}

	/**
	 * @param solicitante
	 *            the solicitante to set
	 */
	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	/**
	 * @return the responsavel
	 */
	public String getResponsavel() {
		return responsavel;
	}

	/**
	 * @param responsavel
	 *            the responsavel to set
	 */
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao
	 *            the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the especialidade
	 */
	public String getEspecialidade() {
		return especialidade;
	}

	/**
	 * @param especialidade
	 *            the especialidade to set
	 */
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * @return the codigoSolicitacaoProntuario
	 */
	public Integer getCodigoSolicitacaoProntuario() {
		return codigoSolicitacaoProntuario;
	}

	/**
	 * @param codigoSolicitacaoProntuario
	 *            the codigoSolicitacaoProntuario to set
	 */
	public void setCodigoSolicitacaoProntuario(Integer codigoSolicitacaoProntuario) {
		this.codigoSolicitacaoProntuario = codigoSolicitacaoProntuario;
	}

	public String getTotalProntuarios() {
		return totalProntuarios;
	}

	public void setTotalProntuarios(String totalProntuarios) {
		this.totalProntuarios = totalProntuarios;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

}
