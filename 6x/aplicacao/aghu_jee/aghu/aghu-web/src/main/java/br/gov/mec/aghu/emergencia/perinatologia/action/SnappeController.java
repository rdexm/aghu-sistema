package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import br.gov.mec.aghu.dominio.DominioApgar5;
import br.gov.mec.aghu.dominio.DominioConvulsoesMultiplas;
import br.gov.mec.aghu.dominio.DominioPHSangue;
import br.gov.mec.aghu.dominio.DominioPesoNascer;
import br.gov.mec.aghu.dominio.DominioPig;
import br.gov.mec.aghu.dominio.DominioPressaoArterialMedia;
import br.gov.mec.aghu.dominio.DominioRazaoPO2PORFIO2;
import br.gov.mec.aghu.dominio.DominioTemperatura;
import br.gov.mec.aghu.dominio.DominioVolumeUnitario;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoSnappesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.perinatologia.vo.SnappeElaboradorVO;
import br.gov.mec.aghu.perinatologia.vo.TabAdequacaoPesoPercentilVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SnappeController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 638726387236826L;

	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "registrarGestacao";
	private static final String REDIRECIONA_RELATORIO_SNAPPE2 = "perinatologia-relatorioSnappe2Pdf";
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private Integer pacCodigoRecemNascido;
	
	private Short seqpRecemNascido;

	private String dadosRecemNascido;

	private Servidor elaborador;

	private McoRecemNascidos recemNascido;

	private McoSnappes snappes;

	private SnappeElaboradorVO vo;
	
	private SnappeElaboradorVO selecionado;

	private List<SnappeElaboradorVO> dataModel;

	private Boolean btNovo;

	private Boolean btGravar;
	
	private Boolean btImprimir;
	
	private Boolean btImprimirDisable;

	private Boolean camposSnappes;

	private Short escore;

	private boolean novoRegistro;

	private boolean mostraModalGravar;

	private List<TabAdequacaoPesoPercentilVO> dataModelTabAdequacaoPesoPercentilVO;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void carregarDados() {
		try {	
			setMostraModalGravar(false);
			obterDadosRecemNascido();
			obterListagens();
			if (isNotListaVazia(getDataModel())) {
				this.selecionado = getDataModel().get(0);
				selecionarItem();
			} else {
				inclusao();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void selecionarItem() {
		McoSnappesId id = this.selecionado.getId();
		
		this.pacCodigoRecemNascido = id.getPacCodigo();
		this.seqpRecemNascido = id.getSeqp();
		
		snappes = emergenciaFacade.obterMcoSnappePorId(id.getPacCodigo(),id.getSeqp());
		escore = snappes.getEscoreSnappeii();
		if (getSnappes() != null) {
			if (verificarSumariaAlta(getSnappes())) {
				if (emergenciaFacade.existeRegistroUsuarioSnappe(getDataModel())) {
					if (emergenciaFacade.verificarUsuarioAlteracaoSnappe(getSnappes())) {
						modoEdicao();
					} else {
						modoEdicaoSemPermissao();
					}
				} else {
					modoEdicaoSemPermissaoSemRegistroUsuarioLogado();
				}
			} else {
				modoEdicaoSemPermissao();
			}

		}
	}

	private boolean verificarSumariaAlta(McoSnappes snappe) {
		boolean SemSumariaAlta = false;
		try {
			SemSumariaAlta = emergenciaFacade.verificarSumariaAlta(snappe);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return SemSumariaAlta;
	}

	public void atualizarEscore() {
		if (snappes != null) {
			Integer escoreInt = emergenciaFacade.calcularSnappe(getSnappes());
			if (escoreInt != null) {
				escore = escoreInt.shortValue();
			}
		}
	}

	public void gravar() {
		if (snappes != null) {
			try {
				snappes.setEscoreSnappeii(getEscore());
				//if(emergenciaFacade.existeAlteracaoSnappe(snappes)){
				
				emergenciaFacade.gravarSnappe(getSnappes(), novoRegistro);
				
				if (novoRegistro) {
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SNAPE_INSERIDO_SUCESSO");
				} else {
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SNAPE_ALTERADO_SUCESSO");
				}
				//}
				novoRegistro = false;
				carregarDados();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

		}
	}
	
	
	public String imprimir(){
		RapServidores servidor = null;
		if (snappes != null) {
			try {
				servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
				if (!CoreUtil.modificados(servidor.getId().getMatricula(), snappes.getSerMatricula()) && !CoreUtil.modificados(snappes.getSerVinCodigo(), servidor.getId().getVinCodigo())) {
					if(emergenciaFacade.existeAlteracaoSnappe(snappes) && !novoRegistro){
						snappes.setEscoreSnappeii(getEscore());
						emergenciaFacade.gravarSnappe(getSnappes(), novoRegistro);
						apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SNAPE_ALTERADO_SUCESSO");
					}
					novoRegistro = false;
					carregarDados();
				}
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
				return null;
			}
		}
		
		return REDIRECIONA_RELATORIO_SNAPPE2;
	}

	public void excluir() {
		if (getVo() != null) {
			try {
				snappes = emergenciaFacade.obterMcoSnappePorId(getVo().getId().getPacCodigo(), getVo().getId().getSeqp());
				verificarSumariaAlta(getSnappes());
				emergenciaFacade.excluirSnappe(getSnappes());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SNAPE_EXCLUIDO_SUCESSO");
				carregarDados();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public String verificarAlteracoes() {
		if (getBtGravar() && emergenciaFacade.verificarAtualizacaoSnappe(getSnappes())) {
			setMostraModalGravar(true);
		}else {
			setMostraModalGravar(false);
			return REDIRECIONA_PESQUISAR_GESTACOES;
		}
		return null;

	}

	private void obterListagens() throws ApplicationBusinessException {
		dataModelTabAdequacaoPesoPercentilVO = emergenciaFacade.listaTabAdequacaoPeso();
		dataModel = emergenciaFacade.listarSnappeElaboradorVO(getPacCodigoRecemNascido());
	}

	private void obterDadosRecemNascido() {
		recemNascido = emergenciaFacade.obterRecemNascidosPorCodigo(getPacCodigoRecemNascido());
		try {
			Paciente paciente = emergenciaFacade.obterPacienteRecemNascido(getPacCodigoRecemNascido());
			dadosRecemNascido = paciente.getNome()
					+ " - "
					+ DateUtil.obterDataFormatada(
							recemNascido.getDthrNascimento(), "dd/MM/yyyy")
					+ " - Prontuário: "
					+ CoreUtil.formataProntuario(paciente.getProntuario())
					+ " - Código: " + getPacCodigoRecemNascido();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void inclusao() {
		try {
			snappes = emergenciaFacade.obterSnappePrePreenchido(getPacCodigoRecemNascido());
			preparaIdNovoSnappe();
			modoInclusao();
			atualizarEscore();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void preparaIdNovoSnappe() {
		Short seqp = 0;
		Short resultado = emergenciaFacade.obterMaxSeqpSnappesPorCodigoPaciente(getPacCodigoRecemNascido());
		seqp = (short) (resultado == null ? 1 : resultado + 1);
		McoSnappesId id = new McoSnappesId(getPacCodigoRecemNascido(), seqp);
		getSnappes().setId(id);
		novoRegistro = true;
	}

	public String gravarAlteracao() {
		gravar();
		return REDIRECIONA_PESQUISAR_GESTACOES;
	}

	public void modoInclusao() {
		btGravar = Boolean.TRUE;
		btImprimir = Boolean.FALSE;
		btImprimirDisable = Boolean.TRUE;
		btNovo = Boolean.FALSE;
		camposSnappes = Boolean.TRUE;
	}

	public void modoEdicaoSemPermissao() {
		btGravar = Boolean.FALSE;
		btImprimir = Boolean.TRUE;
		btImprimirDisable = Boolean.FALSE;
		btNovo = Boolean.FALSE;
		camposSnappes = Boolean.FALSE;
	}

	public void modoEdicaoSemPermissaoSemRegistroUsuarioLogado() {
		btGravar = Boolean.FALSE;
		btImprimir = Boolean.TRUE;
		btImprimirDisable = Boolean.FALSE;
		btNovo = Boolean.TRUE;
		camposSnappes = Boolean.FALSE;
	}

	public void modoEdicao() {
		btGravar = Boolean.TRUE;
		btNovo = Boolean.FALSE;
		btImprimir = Boolean.TRUE;
		btImprimirDisable = Boolean.FALSE;
		camposSnappes = Boolean.TRUE;
	}

	public void modoNovo() {
		btGravar = Boolean.TRUE;
		btImprimir = Boolean.FALSE;
		btImprimirDisable = Boolean.TRUE;
		btNovo = Boolean.FALSE;
		camposSnappes = Boolean.FALSE;
	}

	@SuppressWarnings("rawtypes")
	private Boolean isNotListaVazia(List lista) {
		return lista != null && !lista.isEmpty();
	}

	public DominioPressaoArterialMedia[] listarPresaoArterialMedia() {
		return DominioPressaoArterialMedia.values();
	}

	public DominioTemperatura[] listarTemperatura() {
		return DominioTemperatura.values();
	}

	public DominioRazaoPO2PORFIO2[] listarRazaoPO2PORFIO2() {
		return DominioRazaoPO2PORFIO2.values();

	}

	public DominioPHSangue[] listarDominioPHSangue() {
		return DominioPHSangue.values();
	}

	public DominioConvulsoesMultiplas[] listarDominioConvulsoesMultiplas() {
		return DominioConvulsoesMultiplas.values();
	}

	public DominioVolumeUnitario[] listarDominioVolumeUnitario() {
		return DominioVolumeUnitario.values();
	}

	public DominioPesoNascer[] listarDominioPesoNascer() {
		return DominioPesoNascer.values();
	}

	public DominioPig[] listarDominioPig() {
		return DominioPig.values();
	}

	public DominioApgar5[] listarDominioApgar5() {
		return DominioApgar5.values();
	}

	public String voltar() {
		return REDIRECIONA_PESQUISAR_GESTACOES;
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

	public McoSnappes getSnappes() {
		return snappes;
	}

	public void setSnappes(McoSnappes snappes) {
		this.snappes = snappes;
	}

	public List<SnappeElaboradorVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<SnappeElaboradorVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getPacCodigoRecemNascido() {
		return pacCodigoRecemNascido;
	}

	public void setPacCodigoRecemNascido(Integer pacCodigoRecemNascido) {
		this.pacCodigoRecemNascido = pacCodigoRecemNascido;
	}

	public List<TabAdequacaoPesoPercentilVO> getDataModelTabAdequacaoPesoPercentilVO() {
		return dataModelTabAdequacaoPesoPercentilVO;
	}

	public void setDataModelTabAdequacaoPesoPercentilVO(
			List<TabAdequacaoPesoPercentilVO> dataModelTabAdequacaoPesoPercentilVO) {
		this.dataModelTabAdequacaoPesoPercentilVO = dataModelTabAdequacaoPesoPercentilVO;
	}

	public Boolean getBtNovo() {
		return btNovo;
	}

	public void setBtNovo(Boolean btNovo) {
		this.btNovo = btNovo;
	}

	public Boolean getBtGravar() {
		return btGravar;
	}

	public void setBtGravar(Boolean btGravar) {
		this.btGravar = btGravar;
	}

	public Boolean getBtImprimir() {
		return btImprimir;
	}

	public void setBtImprimir(Boolean btImprimir) {
		this.btImprimir = btImprimir;
	}

	public Boolean getCamposSnappes() {
		return camposSnappes;
	}

	public void setCamposSnappes(Boolean camposSnappes) {
		this.camposSnappes = camposSnappes;
	}

	public Short getEscore() {
		return escore;
	}

	public void setEscore(Short escore) {
		this.escore = escore;
	}

	public boolean isNovoRegistro() {
		return novoRegistro;
	}

	public void setNovoRegistro(boolean novoRegistro) {
		this.novoRegistro = novoRegistro;
	}

	public SnappeElaboradorVO getVo() {
		return vo;
	}

	public void setVo(SnappeElaboradorVO vo) {
		this.vo = vo;
	}

	public SnappeElaboradorVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(SnappeElaboradorVO selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isMostraModalGravar() {
		return mostraModalGravar;
	}

	public void setMostraModalGravar(boolean mostraModalGravar) {
		this.mostraModalGravar = mostraModalGravar;
	}

	public Short getSeqpRecemNascido() {
		return seqpRecemNascido;
	}

	public void setSeqpRecemNascido(Short seqpRecemNascido) {
		this.seqpRecemNascido = seqpRecemNascido;
	}

	public Boolean getBtImprimirDisable() {
		return btImprimirDisable;
	}

	public void setBtImprimirDisable(Boolean btImprimirDisable) {
		this.btImprimirDisable = btImprimirDisable;
	}

	public McoRecemNascidos getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(McoRecemNascidos recemNascido) {
		this.recemNascido = recemNascido;
	}
}
