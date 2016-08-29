package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAgenda;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaTipoSessao;
import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastraTiposSessaoController extends ActionController {

	private static final long serialVersionUID = -8448305280873346219L;

	private static final String PAGE_PESQUISAR_TIPOS_SESSAO = "pesquisaTiposSessao";
	
	private static final String PAGE_CADASTRA_TIPOS_SESSAO = "cadastraTiposSessao";
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private MptTipoSessao mptTipoSessao;
	
	private MptCaracteristica mptCaracteristica;
	
	private Boolean indSituacao;
	private Boolean segunda;
	private Boolean terca;
	private Boolean quarta;
	private Boolean quinta;
	private Boolean sexta;
	private Boolean sabado;
	private Boolean domingo;
	private Boolean manha;
	private Boolean tarde;
	private Boolean noite;
	private Date horaInicialManha;
	private Date horaFinalManha;
	private Date horaInicialTarde;
	private Date horaFinalTarde;
	private Date horaInicialNoite;
	private Date horaFinalNoite;
	
	private  List<MptCaracteristicaTipoSessao> listaMptCaracteristicasTipoSessao;
	
	private boolean edicaoAtiva;
	
	private List<MptCaracteristica> listaMptCaracteristica;
	
	private MptCaracteristicaTipoSessao selecionadoMptCaracteristica;
	
	private MptCaracteristica mptCaracteristicaSG;
	
	private MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao;
	
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	
	public void inicio() {
		if (this.mptTipoSessao != null) {
			this.mptTipoSessao = this.procedimentoTerapeuticoFacade.obterMptTipoSessaoPorSeq(this.mptTipoSessao.getSeq());
			this.indSituacao = this.mptTipoSessao.getIndSituacao().isAtivo();	
			popularDiasSemana();
			popularTurnos();
			if(this.edicaoAtiva== true){
				this.listaMptCaracteristicasTipoSessao = this.procedimentoTerapeuticoFacade.obterCaracteristicaTipoSessaoPorTpsSeq(this.mptTipoSessao);
				}
			} else {
			this.mptTipoSessao = new MptTipoSessao();
			this.indSituacao = Boolean.TRUE;
			this.mptTipoSessao.setTipoAgenda(DominioTipoAgenda.L);
			
		}
	}
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorCaracteristica(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorCaracteristica(strPesquisa, null),
				this.aghuFacade.listarUnidadesFuncionaisPorCaracteristicaCount(strPesquisa, null));
	}
	
	private void popularDiasSemana() {
		List<MptDiaTipoSessao> listaDias = this.procedimentoTerapeuticoFacade.obterDiasPorTipoSessao(this.mptTipoSessao.getSeq());
		for (MptDiaTipoSessao item : listaDias) {
			switch (item.getDia()) {
			case (byte) 1:
				this.domingo = Boolean.TRUE;
				break;
			case (byte) 2:
				this.segunda = Boolean.TRUE;
				break;
			case (byte) 3:
				this.terca = Boolean.TRUE;
				break;
			case (byte) 4:
				this.quarta = Boolean.TRUE;
				break;
			case (byte) 5:
				this.quinta = Boolean.TRUE;
				break;
			case (byte) 6:
				this.sexta = Boolean.TRUE;
				break;
			case (byte) 7:
				this.sabado = Boolean.TRUE;
				break;
			}
		}
	}
	
	private void popularTurnos() {
		List<MptTurnoTipoSessao> listaTurnos = this.procedimentoTerapeuticoFacade.obterTurnosPorTipoSessao(this.mptTipoSessao.getSeq());
		for (MptTurnoTipoSessao item : listaTurnos) {
			switch (item.getTurno()) {
			case M:
				this.manha = Boolean.TRUE;
				this.horaInicialManha = item.getHoraInicio();
				this.horaFinalManha = item.getHoraFim();
				break;
			case T:
				this.tarde = Boolean.TRUE;
				this.horaInicialTarde = item.getHoraInicio();
				this.horaFinalTarde = item.getHoraFim();
				break;
			case N:
				this.noite = Boolean.TRUE;
				this.horaInicialNoite = item.getHoraInicio();
				this.horaFinalNoite = item.getHoraFim();
				break;
			}
		}
	}
	
	public void atualizarTempoFixo() {
		if (this.mptTipoSessao.getTipoAgenda().equals(DominioTipoAgenda.L)) {
			this.mptTipoSessao.setTempoFixo(null);
		}
	}
	
	public void confirmar() {
		try {
			this.mptTipoSessao.setIndSituacao(this.indSituacao.equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);
			if (this.mptTipoSessao.getSeq() == null) {
				// Valida os horÃ¡rios dos turnos antes de inserir
				this.procedimentoTerapeuticoFacade.validarConflitosPeriodo(this.manha, this.tarde, this.noite, this.horaInicialManha,
						this.horaFinalManha, this.horaInicialTarde, this.horaFinalTarde, this.horaInicialNoite, this.horaFinalNoite);
				
				this.procedimentoTerapeuticoFacade.inserirMptTipoSessao(this.mptTipoSessao);
				
				// Valida alteraÃ§Ãµes nos dias de atendimento
				this.procedimentoTerapeuticoFacade.validarMptDiaTipoSessao(this.segunda, this.terca, this.quarta,
						this.quinta, this.sexta, this.sabado, this.domingo, this.mptTipoSessao.getSeq());
				
				// Valida alteraÃ§Ãµes nos turnos.
				this.procedimentoTerapeuticoFacade.validarMptTurnoTipoSessao(this.manha, this.tarde, this.noite,
						this.horaInicialManha, this.horaFinalManha, this.horaInicialTarde, this.horaFinalTarde,
						this.horaInicialNoite, this.horaFinalNoite, this.mptTipoSessao.getSeq());
				this.edicaoAtiva = true;
				this.listaMptCaracteristicasTipoSessao = new ArrayList<MptCaracteristicaTipoSessao>();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_TIPO_SESSAO",
						this.mptTipoSessao.getDescricao());
			} else {
				// Valida os horÃ¡rios dos turnos antes de inserir
				this.procedimentoTerapeuticoFacade.validarConflitosPeriodo(this.manha, this.tarde, this.noite, this.horaInicialManha,
						this.horaFinalManha, this.horaInicialTarde, this.horaFinalTarde, this.horaInicialNoite, this.horaFinalNoite);
				
				this.procedimentoTerapeuticoFacade.atualizarMptTipoSessao(this.mptTipoSessao);
				
				// Valida alteraÃ§Ãµes nos dias de atendimento
				this.procedimentoTerapeuticoFacade.validarMptDiaTipoSessao(this.segunda, this.terca, this.quarta,
						this.quinta, this.sexta, this.sabado, this.domingo, this.mptTipoSessao.getSeq());
				
				// Valida alteraÃ§Ãµes nos turnos.
				this.procedimentoTerapeuticoFacade.validarMptTurnoTipoSessao(this.manha, this.tarde, this.noite,
						this.horaInicialManha, this.horaFinalManha, this.horaInicialTarde, this.horaFinalTarde,
						this.horaInicialNoite, this.horaFinalNoite, this.mptTipoSessao.getSeq());
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_TIPO_SESSAO",
						this.mptTipoSessao.getDescricao());
			}		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Long obterCaracteristicaDescricaoOuSiglaCount(String strPesquisa){
		return this.procedimentoTerapeuticoFacade.obterCaracteristicaDescricaoOuSiglaCount(strPesquisa);
	}
	
	public List<MptCaracteristica> listarCaracteristicaTipoSessao(String strPesquisa) {
 		 return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.obterListaCaracteristicaDescricao(strPesquisa), this.procedimentoTerapeuticoFacade.listarCaracteristicaCount(strPesquisa));
	}
	
	public String adicionar() throws ApplicationBusinessException{
		if((this.mptCaracteristicaSG == null)){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ADICIONAR_CARACTERISTICA_VAZIA");
			return PAGE_CADASTRA_TIPOS_SESSAO;
		}else{
			if(this.procedimentoTerapeuticoFacade.obterVinculoCaracteristicaTipoSessao(this.mptCaracteristicaSG,this.mptTipoSessao)==null){
				this.procedimentoTerapeuticoFacade.adicionarMptCaracteristicaTipoSessao(this.mptTipoSessao, this.mptCaracteristicaSG);	
				this.mptCaracteristicaSG = null;
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ADICIONAR_CARACTERISTICA_SUCESSO");
			}else{
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ADICIONAR_CARACTERISTICA_FALHA");
			
			}
		}
		return PAGE_CADASTRA_TIPOS_SESSAO;
	}
	
	public String excluir(){
		this.procedimentoTerapeuticoFacade.excluirMptCaracteristicaTipoSessao(this.mptCaracteristicaTipoSessao);
		this.listaMptCaracteristicasTipoSessao = this.procedimentoTerapeuticoFacade.obterCaracteristicaTipoSessaoPorTpsSeq(mptTipoSessao);
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CARACTERISTICA_SESSAO_EXCLUSAO");
		return PAGE_CADASTRA_TIPOS_SESSAO;
	}
	
	
	public String cancelar() {
		limpar();
		this.listaMptCaracteristica = new ArrayList<MptCaracteristica>();
		this.mptCaracteristicaSG = null;
		this.edicaoAtiva = false;
		return PAGE_PESQUISAR_TIPOS_SESSAO;
	}
	
	private void limpar() {
		this.mptTipoSessao = null;
		this.indSituacao = null;
		this.segunda = null;
		this.terca = null;
		this.quarta = null;
		this.quinta = null;
		this.sexta = null;
		this.sabado = null;
		this.domingo = null;
		this.manha = null;
		this.tarde = null;
		this.noite = null;
		this.horaInicialManha = null;
		this.horaFinalManha = null;
		this.horaInicialTarde = null;
		this.horaFinalTarde = null;
		this.horaInicialNoite = null;
		this.horaFinalNoite = null;
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}
	

	
	public MptTipoSessao getMptTipoSessao() {
		return mptTipoSessao;
	}

	public void setMptTipoSessao(MptTipoSessao mptTipoSessao) {
		this.mptTipoSessao = mptTipoSessao;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public MptCaracteristica getMptCaracteristica() {
		return mptCaracteristica;
	}
	public void setMptCaracteristica(MptCaracteristica mptCaracteristica) {
		this.mptCaracteristica = mptCaracteristica;
	}
	public List<MptCaracteristicaTipoSessao> getListaMptCaracteristicasTipoSessao() {
		return listaMptCaracteristicasTipoSessao;
	}
	public void setListaMptCaracteristicasTipoSessao(
			List<MptCaracteristicaTipoSessao> listaMptCaracteristicasTipoSessao) {
		this.listaMptCaracteristicasTipoSessao = listaMptCaracteristicasTipoSessao;
	}
	public boolean isEdicaoAtiva() {
		return edicaoAtiva;
	}
	public void setEdicaoAtiva(boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}
	public List<MptCaracteristica> getListaMptCaracteristica() {
		return listaMptCaracteristica;
	}
	public void setListaMptCaracteristica(List<MptCaracteristica> listaMptCaracteristica) {
		this.listaMptCaracteristica = listaMptCaracteristica;
	}
	public MptCaracteristicaTipoSessao getSelecionadoMptCaracteristica() {
		return selecionadoMptCaracteristica;
	}
	public void setSelecionadoMptCaracteristica(
			MptCaracteristicaTipoSessao selecionadoMptCaracteristica) {
		this.selecionadoMptCaracteristica = selecionadoMptCaracteristica;
	}
	public MptCaracteristica getMptCaracteristicaSG() {
		return mptCaracteristicaSG;
	}
	public void setMptCaracteristicaSG(MptCaracteristica mptCaracteristicaSG) {
		this.mptCaracteristicaSG = mptCaracteristicaSG;
	}

	public void setSegunda(Boolean segunda) {
		this.segunda = segunda;
	}

	public Boolean getTerca() {
		return terca;
	}

	public void setTerca(Boolean terca) {
		this.terca = terca;
	}

	public Boolean getQuarta() {
		return quarta;
	}

	public void setQuarta(Boolean quarta) {
		this.quarta = quarta;
	}

	public Boolean getQuinta() {
		return quinta;
	}

	public void setQuinta(Boolean quinta) {
		this.quinta = quinta;
	}

	public Boolean getSexta() {
		return sexta;
	}

	public void setSexta(Boolean sexta) {
		this.sexta = sexta;
	}

	public Boolean getSabado() {
		return sabado;
	}

	public void setSabado(Boolean sabado) {
		this.sabado = sabado;
	}

	public Boolean getDomingo() {
		return domingo;
	}

	public void setDomingo(Boolean domingo) {
		this.domingo = domingo;
	}

	public Boolean getManha() {
		return manha;
	}

	public void setManha(Boolean manha) {
		this.manha = manha;
	}

	public Boolean getTarde() {
		return tarde;
	}

	public void setTarde(Boolean tarde) {
		this.tarde = tarde;
	}

	public Boolean getNoite() {
		return noite;
	}

	public void setNoite(Boolean noite) {
		this.noite = noite;
	}

	public Date getHoraInicialManha() {
		return horaInicialManha;
	}

	public void setHoraInicialManha(Date horaInicialManha) {
		this.horaInicialManha = horaInicialManha;
	}

	public Date getHoraFinalManha() {
		return horaFinalManha;
	}

	public void setHoraFinalManha(Date horaFinalManha) {
		this.horaFinalManha = horaFinalManha;
	}

	public Date getHoraInicialTarde() {
		return horaInicialTarde;
	}

	public void setHoraInicialTarde(Date horaInicialTarde) {
		this.horaInicialTarde = horaInicialTarde;
	}

	public Date getHoraFinalTarde() {
		return horaFinalTarde;
	}

	public void setHoraFinalTarde(Date horaFinalTarde) {
		this.horaFinalTarde = horaFinalTarde;
	}

	public Date getHoraInicialNoite() {
		return horaInicialNoite;
	}

	public void setHoraInicialNoite(Date horaInicialNoite) {
		this.horaInicialNoite = horaInicialNoite;
	}

	public Date getHoraFinalNoite() {
		return horaFinalNoite;
	}

	public void setHoraFinalNoite(Date horaFinalNoite) {
		this.horaFinalNoite = horaFinalNoite;
	}
	
	public Boolean getSegunda() {
		return segunda;
	}
	public MptCaracteristicaTipoSessao getMptCaracteristicaTipoSessao() {
		return mptCaracteristicaTipoSessao;
	}
	public void setMptCaracteristicaTipoSessao(
			MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao) {
		this.mptCaracteristicaTipoSessao = mptCaracteristicaTipoSessao;
	}

}
