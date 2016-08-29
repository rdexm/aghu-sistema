package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

public class CadastroOutrosAtestadosController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7382889325084338033L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
//	@Inject
//	private RelatorioAtestadosController relatorioAtestadosController;
	
	// @EJB
	// private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private boolean emEdicao = false;

	private List<MamAtestados> listaAtestados;

	// selecao da grid
	private MamAtestados selecao;
	// Workaround para funcionar o atributo selection da tabela.
	private MamAtestados itemAux;
	private MamAtestados item;
	private MamTipoAtestado mamTipoAtestado;
	private String pUso;
	private Long atsSeq;

	private MpmAltaSumario altaSumario;
	
	/** #46218 */
	private String listaOrigem;
	private final static String ATESTADO = "ATESTADO";
	
	private final static String ALTA = "ALTA";
	
	private Integer atdSeq;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		try {
			AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ATESTADO_OUTROS);
			if (param != null) {
				this.mamTipoAtestado = this.ambulatorioFacade.obterMamTipoAtestadoPorChavePrimaria(Short.valueOf(param.getVlrNumerico().toString()));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
		this.item = new MamAtestados();
		this.item.setNroVias(Byte.valueOf("1"));
	}

	public void adicionar() {
		if ("S".equals(pUso)) { // Sumário de Alta
			this.item.setAltaSumario(altaSumario);
			this.item.setIndPendente(DominioIndPendenteAmbulatorio.P);
		} else if ("O".equals(pUso)) {
			this.item.setIndPendente(DominioIndPendenteAmbulatorio.P); // CO
		} else {
			if (this.item.getIndPendente() == null) {
				this.item.setIndPendente(DominioIndPendenteAmbulatorio.R);
			}
		}
		
		if(listaOrigem !=null && this.listaOrigem.trim().equals(ATESTADO)){
			this.item.setAltaSumario(null);
			this.item.setAtendimento(this.aghuFacade.obterAghAtendimentoPorSeq(this.atdSeq));
		}
		else if(listaOrigem !=null && this.listaOrigem.trim().equals(ALTA)) {
			this.item.setAltaSumario(this.altaSumario);
		}
		
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(this.altaSumario.getPaciente().getCodigo());
		this.item.setAipPacientes(paciente);

		this.item.setMamTipoAtestado(mamTipoAtestado);
		this.item.setIndImpresso(Boolean.FALSE);

		try {
			/** #46218 */
//			if(listaOrigem.trim().equals(ATESTADO)){
//				this.item.setAltaSumario(null);
//				this.item.setAtendimento(new AghAtendimentos());
//				this.item.getAtendimento().setSeq(this.atsSeq.intValue());
//			}
			this.ambulatorioFacade.persistirMamAtestado(this.item, false);
			pesquisar();
			this.limpar();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_ATESTADO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void alterar() {
		try {
			this.ambulatorioFacade.persistirMamAtestado(this.item, false);
			pesquisar();
			this.item = new MamAtestados();
			this.emEdicao = false;
			this.atsSeq = null;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ATESTADO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void remover() {
		try {
			this.ambulatorioFacade.excluirMamAtestado(this.atsSeq);
			pesquisar();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ATESTADO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		
		if(this.listaOrigem != null && this.listaOrigem.trim().equals(ATESTADO)){
			this.listaAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipo(atdSeq, 
					mamTipoAtestado.getSeq(),null);
		}
		else if(listaOrigem !=null && this.listaOrigem.trim().equals(ALTA)) {
			if(mamTipoAtestado != null && mamTipoAtestado.getSeq() != null){
				this.listaAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipo(null, 
						mamTipoAtestado.getSeq(),altaSumario);
			}
		}
	}

	public void limpar() {
		this.emEdicao = false;
		this.selecao = null;
		this.item = new MamAtestados();
		this.atsSeq = null;
		this.item.setNroVias(Byte.valueOf("1"));
	}

	public void cancelar() {
		this.emEdicao = false;
		this.limpar();
	}

	public void editar() {
		this.item = this.ambulatorioFacade.obterMamAtestadoPorChavePrimaria(this.atsSeq);
		this.emEdicao = true;
	}
	
	/**
	 * #46252 - Chama a impressão do relatório #46485
	 */
	public void imprimirAtestado() {
//		if (this.selecao != null) {
//			try {
//				AtestadoVO atestado = this.prescricaoMedicaFacade
//						.obterDocumentoPacienteAtestado(this.selecao.getSeq(), false);
//				
//				this.relatorioAtestadosController.setAtestado(atestado);
//				this.relatorioAtestadosController.setDescricaoDocumento("Outros Atestados");
//				this.relatorioAtestadosController.directPrint();
//			} catch (ApplicationBusinessException e) {
//				apresentarExcecaoNegocio(e);
//			}
//		} else {
//			apresentarMsgNegocio(Severity.INFO, "MAM_SELECIONE_ATESTADO_IMPRIMIR");
//		}
	}
	
	//getters e setters
	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public List<MamAtestados> getListaAtestados() {
		return listaAtestados;
	}

	public void setListaAtestados(List<MamAtestados> listaAtestados) {
		this.listaAtestados = listaAtestados;
	}

	public MamAtestados getSelecao() {
		return selecao;
	}

	public void setSelecao(MamAtestados selecao) {
		this.selecao = selecao;
	}

	public MamAtestados getItemAux() {
		return itemAux;
	}

	public void setItemAux(MamAtestados itemAux) {
		this.itemAux = itemAux;
	}

	public MamAtestados getItem() {
		return item;
	}

	public void setItem(MamAtestados item) {
		this.item = item;
	}

	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public MamTipoAtestado getMamTipoAtestado() {
		return mamTipoAtestado;
	}

	public void setMamTipoAtestado(MamTipoAtestado mamTipoAtestado) {
		this.mamTipoAtestado = mamTipoAtestado;
	}

	public String getpUso() {
		return pUso;
	}

	public void setpUso(String pUso) {
		this.pUso = pUso;
	}

	public Long getAtsSeq() {
		return atsSeq;
	}

	public void setAtsSeq(Long atsSeq) {
		this.atsSeq = atsSeq;
	}

	public String getListaOrigem() {
		return listaOrigem;
	}

	public void setListaOrigem(String listaOrigem) {
		this.listaOrigem = listaOrigem;
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
}
