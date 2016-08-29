package br.gov.mec.aghu.paciente.historico.action;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.historico.business.IHistoricoPacienteFacade;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da tela de historico de paciente
 * 
 */
public class HistoricoPacienteController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3552849422505061611L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IHistoricoPacienteFacade historicoPacienteFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private Integer prontuario;

	private Integer codigo;

	
	private boolean permitirFiltrar = false;

	private boolean realizouPesquisa = false;

	private HistoricoPacienteVO historicoPaciente;

	private AipPacientes aipPaciente;

	private SimpleDateFormat sdf = new SimpleDateFormat();;

	private String cameFrom;
	
	private Boolean consideraDigito = false;
	
	private Boolean exibeCheckbox;

	private static final String REDIRECT_CADASTRO_PACIENTE = "cadastroPaciente";
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void prepararHistorico() {
		this.consideraDigito = true;
		this.exibeCheckbox = aghuFacade.isHCPA();
		
	    if (this.codigo != null || this.prontuario != null) {
	    	permitirFiltrar = false;
	    	this.pesquisarHistorico();	    	
	    } else {
	    	permitirFiltrar = true;
	    }
		
		if (!permitirFiltrar && this.codigo == null && this.prontuario == null) {
			// Gera mensagem de erro (filtro não informado)
			this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_PESQUISA_HISTORICO_PACIENTE_FILTRO_NAO_INFORMADO");
		}
	}
	
	public void limparCampos() {
		this.consideraDigito = true;
		this.prontuario = null;
		limparPesquisa();
	}

	/**
	 * Metodo que busca um historico de paciente pelo seu prontuario/codigo. Eh
	 * executado na ação do botao pesquisa na tela de historico do paciente.
	 */
	public void pesquisarHistorico() {
		this.realizouPesquisa = true;
		this.historicoPaciente = null;
		this.aipPaciente = null;
		
		try {
			if (this.consideraDigito) {
				this.aipPaciente = pacienteFacade.obterPacientePorCodigoOuProntuario(this.prontuario, this.codigo, null);
			} else {
				this.aipPaciente = pacienteFacade.obterPacientePorCodigoOuProntuarioSemDigito(this.prontuario, this.codigo);
			}
			if (this.aipPaciente != null) {
				historicoPaciente = historicoPacienteFacade.obterHistoricoPaciente(
						this.aipPaciente.getProntuario(), this.aipPaciente.getCodigo(), true, true);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparPesquisa() {
		this.prontuario = null;
		this.codigo = null;
		this.aipPaciente = null;
		this.historicoPaciente = null;
		this.realizouPesquisa = false;
	}

	/**
	 * Método que renderiza na tela um outputText conforme o valor do atributo
	 * indPacienteVip do paciente.
	 */
	public boolean pacienteVip() {
		boolean vip = false;
		if (historicoPaciente != null
				&& historicoPaciente.getAipPaciente().getIndPacienteVip() != null
				&& historicoPaciente.getAipPaciente().getIndPacienteVip()
						.equals(DominioSimNao.S)) {
			vip = true;
		}
		return vip;
	}

	/**
	 * Método responsável por retornar a data de óbito ou a data de óbito
	 * externo e formatar conforme o tipo de data(DominioTipoDataObito).
	 * 
	 * @return data formatada.
	 */
	public String getData() {
		if (historicoPaciente != null) {
			if (historicoPaciente.getAipPaciente().getDtObito() != null) {
				sdf.applyPattern("dd/MM/yyyy");
				return sdf.format(historicoPaciente.getAipPaciente()
						.getDtObito());
			} else {
				if (historicoPaciente.getAipPaciente().getTipoDataObito() != null) {
					if (historicoPaciente.getAipPaciente().getTipoDataObito() == DominioTipoDataObito.IGN) {
						return "";
					} else {
						sdf.applyPattern(historicoPaciente.getAipPaciente()
								.getTipoDataObito().getPattern());
						return sdf.format(historicoPaciente.getAipPaciente()
								.getDtObitoExterno());
					}
				}
				return "";
			}
		}
		return "";
	}

	/**
	 * Método responsável por retornar a descrição da data: Óbito, Óbito externo
	 * ou Data ignorada - Óbito externo.
	 * 
	 * @return descrição da data.
	 */
	public String getDescData() {
		if (historicoPaciente != null) {
			if (historicoPaciente.getAipPaciente().getDtObito() != null) {
				return WebUtil.initLocalizedMessage("LABEL_OBITO", null);
			} else {
				if (historicoPaciente.getAipPaciente().getTipoDataObito() != null) {
					if (historicoPaciente.getAipPaciente().getTipoDataObito() == DominioTipoDataObito.IGN) {
						return WebUtil.initLocalizedMessage(
								"LABEL_OBITO_IGNORADO", null);
					} else {
						return WebUtil.initLocalizedMessage(
								"LABEL_OBITO_EXTERNO", null);
					}
				}
				return "";
			}
		}
		return "";
	}

	public String cancelarHistoricoPaciente() {
		if ("cadastroPaciente".equalsIgnoreCase(cameFrom)) {
//			<param name="aip_pac_codigo" value="#{historicoPacienteController.codigo}" />
			return REDIRECT_CADASTRO_PACIENTE;
		} else {
			return null;
		}
	}
	
	// ### GETs e SETs ###
	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public HistoricoPacienteVO getHistoricoPaciente() {
		return historicoPaciente;
	}

	public void setHistoricoPaciente(HistoricoPacienteVO historicoPaciente) {
		this.historicoPaciente = historicoPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public boolean isPermitirFiltrar() {
		return permitirFiltrar;
	}

	public void setPermitirFiltrar(boolean permitirFiltrar) {
		this.permitirFiltrar = permitirFiltrar;
	}

	public boolean isRealizouPesquisa() {
		return realizouPesquisa;
	}

	public void setRealizouPesquisa(boolean realizouPesquisa) {
		this.realizouPesquisa = realizouPesquisa;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String obterProntuarioFormatado(Object valor) {
		return CoreUtil.formataProntuario(valor);
	}

	/**
	 * @return the consideraDigito
	 */
	public Boolean getConsideraDigito() {
		return consideraDigito;
	}

	/**
	 * @param consideraDigito the consideraDigito to set
	 */
	public void setConsideraDigito(Boolean consideraDigito) {
		this.consideraDigito = consideraDigito;
	}

	/**
	 * @return the exibeCheckbox
	 */
	public Boolean getExibeCheckbox() {
		return exibeCheckbox;
	}

	/**
	 * @param exibeCheckbox the exibeCheckbox to set
	 */
	public void setExibeCheckbox(Boolean exibeCheckbox) {
		this.exibeCheckbox = exibeCheckbox;
	}
	
	

}
