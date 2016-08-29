package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroOutrosAtestadosAmbulatorioController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7382889325084338033L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private boolean emEdicao = false;

	private List<MamAtestados> listaAtestados;

	// selecao da grid
	private MamAtestados selecao;
	// Workaround para funcionar o atributo selection da tabela.
	private MamAtestados itemAux;
	private MamAtestados item;
	private MamTipoAtestado mamTipoAtestado;
	private Long atsSeq;

	private AacConsultas consultaSelecionada;
	
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
		
		if(this.item.getObservacao().length() > 2000){
			this.apresentarMsgNegocio(Severity.ERROR, "LABEL_ATESTADO_MENSAGEM_VALIDACAO_OUTROS");
		} else {
			this.item.setObservacao(this.item.getObservacao().trim());
			if(this.item.getObservacao().equals(StringUtils.EMPTY)){
				this.apresentarMsgNegocio(Severity.ERROR, "LABEL_ATESTADO_MENSAGEM_CAMPO_OBRIGATORIO_DESCRICAO");
			} else {
				AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(this.consultaSelecionada.getPaciente().getCodigo());
				this.item.setAipPacientes(paciente);
				
				this.item.setIndPendente(DominioIndPendenteAmbulatorio.P);
				this.item.setIndImpresso(Boolean.FALSE);
				this.item.setMamTipoAtestado(mamTipoAtestado);
				this.item.setDthrCons(this.consultaSelecionada.getDtConsulta());
				this.item.setConsulta(this.consultaSelecionada);
				
				try {
					this.ambulatorioFacade.validarValorMinimoNumeroVias(item);
					this.ambulatorioFacade.persistirMamAtestadoAmbulatorio(this.item);
					pesquisar();
					this.limpar();
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_ATESTADO");
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}

	public void alterar() {
		try {
			this.ambulatorioFacade.validarValorMinimo(item);
			this.ambulatorioFacade.persistirMamAtestadoAmbulatorio(this.item);
			pesquisar();
			this.limpar();
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
		
		this.listaAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipoAtendimento(
				consultaSelecionada.getNumero(), mamTipoAtestado.getSeq());
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
	
	public boolean editandoRegistro(Long seq){
		if(seq !=null && seq.equals(atsSeq)){
			return true;
		}
		return false;
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

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public MamTipoAtestado getMamTipoAtestado() {
		return mamTipoAtestado;
	}

	public void setMamTipoAtestado(MamTipoAtestado mamTipoAtestado) {
		this.mamTipoAtestado = mamTipoAtestado;
	}

	public Long getAtsSeq() {
		return atsSeq;
	}

	public void setAtsSeq(Long atsSeq) {
		this.atsSeq = atsSeq;
	}
}
