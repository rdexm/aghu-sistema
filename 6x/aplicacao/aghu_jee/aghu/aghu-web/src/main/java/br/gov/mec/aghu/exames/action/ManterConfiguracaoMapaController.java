package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioListaClasseRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class ManterConfiguracaoMapaController extends ActionController {


	private static final long serialVersionUID = 7565787291945785469L;

	private static final String MANTER_CONFIG_MAPA_EXAME = "manterConfigMapaExame";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private AelConfigMapa filtros;
	
	private List<AelConfigMapa> lista;
	
	//Para Adicionar itens
	private AelConfigMapa aelConfigMapa;
	
	private AelConfigMapa aelConfigMapaSelect;
	
	private boolean editando;

	private boolean ativo;
	
	private DominioListaClasseRelatorio reportConfigMapaSelecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		preparaInsert();
		filtros = new AelConfigMapa();
	}

	private void preparaInsert() {
		aelConfigMapa = new AelConfigMapa();
		aelConfigMapa.setOrigem(DominoOrigemMapaAmostraItemExame.A);
		aelConfigMapa.setIndSituacao(DominioSituacao.A);
	}
	
	public void pesquisar() {
		lista = examesFacade.pesquisarAelConfigMapa(filtros);
		editando = false;
		preparaInsert();
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		filtros = new AelConfigMapa(); 
		lista = null;
		editando = false;
		reportConfigMapaSelecionado = null;
		preparaInsert();
	}
	
	public void gravar() {
		try {
			aelConfigMapa.setReport(this.reportConfigMapaSelecionado.getCodigo());
			
			if(aelConfigMapa.getSeq() != null){
				examesFacade.persistirAelConfigMapa(aelConfigMapa);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_CONFIGURACAO_MAPA_UPDATE_SUCESSO", aelConfigMapa.getNomeMapa());
				cancelarEdicao();

			} else {
				examesFacade.persistirAelConfigMapa(aelConfigMapa);
				apresentarMsgNegocio( Severity.INFO, "MENSAGEM_CONFIGURACAO_MAPA_INSERT_SUCESSO", aelConfigMapa.getNomeMapa());
			}

			preparaInsert();
			reportConfigMapaSelecionado = null;
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}
		
		pesquisar();
	}
	
	public void editar() {
		this.editando = true;
		reportConfigMapaSelecionado = null;
		if(aelConfigMapa.getReport() != null) {
			for(DominioListaClasseRelatorio classeRel : DominioListaClasseRelatorio.values()) {
				if(classeRel.getCodigo().equals(aelConfigMapa.getReport())) {
					reportConfigMapaSelecionado = classeRel;
					break;
				}
			}
		}
	}
		
	public void cancelarEdicao() {
		editando = false;
		reportConfigMapaSelecionado = null;
		preparaInsert();
	}		
	
	public void excluir() {
		try {
			examesFacade.removerAelConfigMapa(aelConfigMapaSelect.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONFIGURACAO_MAPA_DELETE_SUCESSO", aelConfigMapaSelect.getNomeMapa());
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
		}		
		pesquisar();
	}
	
	public void ativarInativar() {
		try {
			aelConfigMapaSelect.setIndSituacao( (DominioSituacao.A.equals(aelConfigMapaSelect.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			examesFacade.persistirAelConfigMapa(aelConfigMapaSelect);
			apresentarMsgNegocio( Severity.INFO, 
								    ( DominioSituacao.A.equals(aelConfigMapaSelect.getIndSituacao()) 
								    	? "MENSAGEM_CONFIGURACAO_MAPA_ATIVADO_SUCESSO" 
										: "MENSAGEM_CONFIGURACAO_MAPA_INATIVADO_SUCESSO" 
									), aelConfigMapaSelect.getNomeMapa());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public String manterConfigMapaExame(){
		return MANTER_CONFIG_MAPA_EXAME;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String parametro) {
		return this.aghuFacade.obterUnidadesFuncionais(parametro);
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	} 

	public AelConfigMapa getFiltros() {
		return filtros;
	}

	public void setFiltros(AelConfigMapa filtros) {
		this.filtros = filtros;
	}

	public List<AelConfigMapa> getLista() {
		return lista;
	}

	public void setLista(List<AelConfigMapa> lista) {
		this.lista = lista;
	}

	public AelConfigMapa getAelConfigMapa() {
		return aelConfigMapa;
	}

	public void setAelConfigMapa(AelConfigMapa aelConfigMapa) {
		this.aelConfigMapa = aelConfigMapa;
	}

	public AelConfigMapa getAelConfigMapaSelect() {
		return aelConfigMapaSelect;
	}

	public void setAelConfigMapaSelect(AelConfigMapa aelConfigMapaSelect) {
		this.aelConfigMapaSelect = aelConfigMapaSelect;
	}
	
	public DominioListaClasseRelatorio getReportConfigMapaSelecionado() {
		return reportConfigMapaSelecionado;
	}

	public void setReportConfigMapaSelecionado(
			DominioListaClasseRelatorio reportConfigMapaSelecionado) {
		this.reportConfigMapaSelecionado = reportConfigMapaSelecionado;
	}
}