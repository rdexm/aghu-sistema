package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioFormaOrelha;
import br.gov.mec.aghu.dominio.DominioFormacaoMamilo;
import br.gov.mec.aghu.dominio.DominioGlandulaMamaria;
import br.gov.mec.aghu.dominio.DominioPregasPlantares;
import br.gov.mec.aghu.dominio.DominioTexturaPele;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.CalculoCapurroVO;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class CalculoCapurroController extends ActionController {

	private static final long serialVersionUID = 1424066061060792641L;

	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "registrarGestacao"; 
	
	@EJB 
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Integer pacCodigo;
	
	private McoRecemNascidos recemNascido;
	private String dadosRecemNascido;
	private Servidor elaborador;
	private Byte igSemanas;
	private Byte igDias;
	
	private boolean controlesPreenchidos;
	
	List<CalculoCapurroVO> listaCalculos;
	private CalculoCapurroVO calculoSelecionado;
	private CalculoCapurroVO calculoPreSelecionado;
	private CalculoCapurroVO calculoExcluir;
	
	private boolean modoInclusao;
	private boolean mesmoServidor;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void inicio() {
		this.obterDadosRecemNascido();
	}
	
	private void obterDadosRecemNascido() {
		this.recemNascido  = emergenciaFacade.obterRecemNascidosPorCodigo(this.pacCodigo);
		try {
			Paciente paciente = emergenciaFacade.obterPacienteRecemNascido(pacCodigo);
			String sexo = paciente.getSexo().equalsIgnoreCase("M") ? "Masculino" : "Feminino";
			
			this.dadosRecemNascido = paciente.getNome() + " - " + DateUtil.obterDataFormatada(recemNascido.getDthrNascimento(), "dd/MM/yyyy HH:mm")
					+ " - Prontuário: " + CoreUtil.formataProntuario(paciente.getProntuario())
					+ " - Sexo: " + sexo
					+ " - Código: "  + this.pacCodigo;
		
			this.popularListaCalculos(true);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void popularListaCalculos(boolean selecionarRegistro) throws ApplicationBusinessException {
		this.listaCalculos = emergenciaFacade.listarCalculoCapurrosPorCodigoPaciente(this.pacCodigo);
		if(!this.listaCalculos.isEmpty() && selecionarRegistro) {
			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
			boolean selecionou = false;
			for (CalculoCapurroVO vo : this.listaCalculos) {
				if (vo.getSerMatricula().equals(servidor.getId().getMatricula())
						&& vo.getSerVinCodigo().equals(servidor.getId().getVinCodigo())) {
					this.calculoSelecionado = vo;
					this.elaborador = vo.getElaborador();
					this.igSemanas = vo.getIgSemanas();
					this.igDias = vo.getIgDias();
					this.mesmoServidor = vo.isMesmoServidor();
					selecionou = true;
					break;
				}
			}
			if (!selecionou) {
				this.calculoSelecionado = this.listaCalculos.get(0);
				this.elaborador = this.listaCalculos.get(0).getElaborador();
				this.igSemanas = this.listaCalculos.get(0).getIgSemanas();
				this.igDias = this.listaCalculos.get(0).getIgDias();
				this.mesmoServidor = this.listaCalculos.get(0).isMesmoServidor();
			}
		}
	}
	
	public void verificarServidor() {
		this.mesmoServidor = this.calculoSelecionado.isMesmoServidor();
		this.elaborador = this.calculoSelecionado.getElaborador();
		this.igSemanas = this.calculoSelecionado.getIgSemanas();
		this.igDias = this.calculoSelecionado.getIgDias();
		this.modoInclusao = false;
		this.controlesPreenchidos = true;
	}
	
	public void prepararInclusaoCalculoCapurro() {
		try {
			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
			this.elaborador = this.registroColaboradorFacade
					.obterVRapPessoaServidorPorVinCodMatricula(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
			
			this.controlesPreenchidos = false;
			this.modoInclusao = true;
			this.mesmoServidor = true;
			this.calculoSelecionado = new CalculoCapurroVO();
			this.calculoSelecionado.setPacCodigo(this.pacCodigo);
			this.calculoSelecionado.setMesmoServidor(true);
			this.igSemanas = null;
			this.igDias = null;
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void calculoCapurro() {
		DominioTexturaPele texturaPele = this.calculoSelecionado.getTexturaPele();
		DominioFormaOrelha formaOrelha = this.calculoSelecionado.getFormaOrelha();
		DominioGlandulaMamaria glandulaMamaria = this.calculoSelecionado.getGlandulaMamaria();
		DominioPregasPlantares pregasPlantares = this.calculoSelecionado.getPregasPlantares();
		DominioFormacaoMamilo formacaoMamilo = this.calculoSelecionado.getFormacaoMamilo();
		
		if (texturaPele != null && formaOrelha != null && glandulaMamaria != null
				&& pregasPlantares != null && formacaoMamilo != null) {
			
			this.controlesPreenchidos = true;
			
			this.igSemanas = (byte) ((204 
					+ Integer.valueOf(texturaPele.getCodigo())
					+ Integer.valueOf(formaOrelha.getCodigo())
					+ Integer.valueOf(glandulaMamaria.getCodigo())
					+ Integer.valueOf(pregasPlantares.getCodigo())
					+ Integer.valueOf(formacaoMamilo.getCodigo())) / 7);
			
			this.igDias = (byte) ((204 
					+ Integer.valueOf(texturaPele.getCodigo())
					+ Integer.valueOf(formaOrelha.getCodigo())
					+ Integer.valueOf(glandulaMamaria.getCodigo())
					+ Integer.valueOf(pregasPlantares.getCodigo())
					+ Integer.valueOf(formacaoMamilo.getCodigo())) % 7);
			
		} else {
			this.controlesPreenchidos = false;
		}
	}
	
	public void excluirCalculoCapurro() {
		try {
			this.emergenciaFacade.excluirMcoIddGestCapurros(this.calculoExcluir);
			this.popularListaCalculos(true);
			this.modoInclusao = false;
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CALCULO_CAPURRO_EXCLUIDO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarCalculoCapurro() {
		this.calculoSelecionado.setIgSemanas(this.igSemanas);
		this.calculoSelecionado.setIgDias(this.igDias);
		
		try {
			boolean edicao = this.calculoSelecionado.getElaborador() != null;
			
			this.emergenciaFacade.persistirMcoIddGestCapurros(this.calculoSelecionado);
			this.popularListaCalculos(true);
			this.modoInclusao = false;
			if (edicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CALCULO_CAPURRO_ALTERADO");
				
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CALCULO_CAPURRO_INCLUIDO");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarCalculoCapurroModal() {
		this.calculoSelecionado.setIgSemanas(this.igSemanas);
		this.calculoSelecionado.setIgDias(this.igDias);
		try {
			this.emergenciaFacade.persistirMcoIddGestCapurros(this.calculoSelecionado);
			this.popularListaCalculos(false);
			
			for (CalculoCapurroVO vo : this.listaCalculos) {
				if (vo.getSerMatricula().equals(this.calculoPreSelecionado.getSerMatricula())
						&& vo.getSerVinCodigo().equals(this.calculoPreSelecionado.getSerVinCodigo())
						&& vo.getPacCodigo().equals(this.calculoPreSelecionado.getPacCodigo())) {
					
					this.calculoSelecionado = vo;
					this.elaborador = vo.getElaborador();
					this.igSemanas = vo.getIgSemanas();
					this.igDias = vo.getIgDias();
					this.mesmoServidor = vo.isMesmoServidor();
					this.calculoPreSelecionado = null;
					break;
				}
			}
			this.modoInclusao = false;
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CALCULO_CAPURRO_ALTERADO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarCalculoCapurro() {
		try {
			this.popularListaCalculos(false);
			
			for (CalculoCapurroVO vo : this.listaCalculos) {
				if (vo.getSerMatricula().equals(this.calculoPreSelecionado.getSerMatricula())
						&& vo.getSerVinCodigo().equals(this.calculoPreSelecionado.getSerVinCodigo())
						&& vo.getPacCodigo().equals(this.calculoPreSelecionado.getPacCodigo())) {
					
					this.calculoSelecionado = vo;
					this.elaborador = vo.getElaborador();
					this.igSemanas = vo.getIgSemanas();
					this.igDias = vo.getIgDias();
					this.mesmoServidor = vo.isMesmoServidor();
					this.calculoPreSelecionado = null;
					break;
				}
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar() {
		this.limpar();
		return REDIRECIONA_PESQUISAR_GESTACOES;
	}
	
	public void limpar() {
		this.controlesPreenchidos = false;
		this.modoInclusao = false;
		this.calculoSelecionado = null;
		this.calculoPreSelecionado = null;
		this.igSemanas = null;
		this.igDias = null;
		this.mesmoServidor = false;
		this.elaborador = null;
		this.recemNascido = null;
	}
	
	// Getters e Setters
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public McoRecemNascidos getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(McoRecemNascidos recemNascido) {
		this.recemNascido = recemNascido;
	}

	public String getDadosRecemNascido() {
		return dadosRecemNascido;
	}

	public void setDadosRecemNascido(String dadosRecemNascido) {
		this.dadosRecemNascido = dadosRecemNascido;
	}

	public Servidor getElaborador() {
		return elaborador;
	}

	public void setElaborador(Servidor elaborador) {
		this.elaborador = elaborador;
	}

	public Byte getIgSemanas() {
		return igSemanas;
	}

	public void setIgSemanas(Byte igSemanas) {
		this.igSemanas = igSemanas;
	}

	public Byte getIgDias() {
		return igDias;
	}

	public void setIgDias(Byte igDias) {
		this.igDias = igDias;
	}

	public boolean isControlesPreenchidos() {
		return controlesPreenchidos;
	}

	public void setControlesPreenchidos(boolean controlesPreenchidos) {
		this.controlesPreenchidos = controlesPreenchidos;
	}

	public List<CalculoCapurroVO> getListaCalculos() {
		return listaCalculos;
	}

	public void setListaCalculos(List<CalculoCapurroVO> listaCalculos) {
		this.listaCalculos = listaCalculos;
	}

	public CalculoCapurroVO getCalculoSelecionado() {
		return calculoSelecionado;
	}

	public void setCalculoSelecionado(CalculoCapurroVO calculoSelecionado) {
		this.calculoSelecionado.setIgSemanas(this.igSemanas);
		this.calculoSelecionado.setIgDias(this.igDias);
		if (!this.modoInclusao && this.calculoSelecionado != null && !calculoSelecionado.equals(this.calculoSelecionado)
				&& this.emergenciaFacade.possuiAlteracaoIddGestCapurros(this.calculoSelecionado)) {
			// Salva o cálculo para selecioná-lo após operação na modal.
			this.calculoPreSelecionado = calculoSelecionado;
			super.openDialog("modalConfirmacaoGravarAlteracaoWG");
			
		} else {
			this.calculoSelecionado = calculoSelecionado;
		}
	}

	public CalculoCapurroVO getCalculoPreSelecionado() {
		return calculoPreSelecionado;
	}

	public void setCalculoPreSelecionado(CalculoCapurroVO calculoPreSelecionado) {
		this.calculoPreSelecionado = calculoPreSelecionado;
	}

	public CalculoCapurroVO getCalculoExcluir() {
		return calculoExcluir;
	}

	public void setCalculoExcluir(CalculoCapurroVO calculoExcluir) {
		this.calculoExcluir = calculoExcluir;
	}

	public boolean isModoInclusao() {
		return modoInclusao;
	}

	public void setModoInclusao(boolean modoInclusao) {
		this.modoInclusao = modoInclusao;
	}

	public boolean isMesmoServidor() {
		return mesmoServidor;
	}

	public void setMesmoServidor(boolean mesmoServidor) {
		this.mesmoServidor = mesmoServidor;
	}
}
