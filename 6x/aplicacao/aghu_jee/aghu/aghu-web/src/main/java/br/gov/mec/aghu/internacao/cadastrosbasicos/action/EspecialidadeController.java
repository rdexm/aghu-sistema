package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AGHUUtil;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class EspecialidadeController extends ActionController {

	private final String PAGE_PESQUISAR_ESPECIALIDADE = "especialidadeList";
	
	private static final long serialVersionUID = 6703335194870640647L;
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private List<CaracteristicaEspecialidadeFlag> caracteristicas;
	private Map<DominioCaracEspecialidade, CaracteristicaEspecialidadeFlag> mapCaracteristicas;	
	private AghEspecialidades especialidade;
	private List<AghCaractEspecialidades> listaCaracEspecialidade;
	
	
	
	@PostConstruct
	public void init() {
		
		
		especialidade = new AghEspecialidades();
		listaCaracEspecialidade = new ArrayList<AghCaractEspecialidades>();
	}
	
	
	public void iniciaForm() {
			

			if (this.especialidade.getSeq() == null) {
			this.especialidade = this.cadastrosBasicosInternacaoFacade.gerarNovaEspecialidade();
				especialidade.setIndSituacao(DominioSituacao.A);
			} else {
			Enum[] especialidadeInners = {AghEspecialidades.Fields.CLINICA,AghEspecialidades.Fields.CENTRO_CUSTO};

			Enum[] especialidadeLefts = { AghEspecialidades.Fields.SERVIDOR,
						AghEspecialidades.Fields.ESPECIALIDADE_GENERICA,
										  AghEspecialidades.Fields.SERVIDOR_CHEFIA, AghEspecialidades.Fields.SERVIDOR_CHEFIA_PESSOA_FISICA,
						AghEspecialidades.Fields.ESPECIALIDADE_AGRUPA_LOTE_EXAME,
						AghEspecialidades.Fields.CARACTERISTICAS };

			especialidade = cadastrosBasicosInternacaoFacade.obterAghEspecialidades(this.especialidade.getSeq(), especialidadeInners, especialidadeLefts);
			}
		inicializaCaracteristicas();

		}
	
	
	private void inicializaCaracteristicas() {
		caracteristicas = new ArrayList<CaracteristicaEspecialidadeFlag>();
		mapCaracteristicas = new HashMap<DominioCaracEspecialidade, CaracteristicaEspecialidadeFlag>();
		
		for (DominioCaracEspecialidade dce : DominioCaracEspecialidade.values()) {
			if(!dce.equals(DominioCaracEspecialidade.SELECIONE)){
				CaracteristicaEspecialidadeFlag cef = new CaracteristicaEspecialidadeFlag(dce, false);
				caracteristicas.add(cef);
				mapCaracteristicas.put(dce, cef);			
			}
		}
		if (this.especialidade.getSeq() != null && this.especialidade.getCaracteristicas() != null) {
			for (AghCaractEspecialidades ace : this.especialidade.getCaracteristicas()) {
				mapCaracteristicas.get(ace.getId().getCaracteristica()).setValor(true);
			}		
		}
		AGHUUtil.ordenarLista(caracteristicas, "caracteristica.descricao", true);
	}
	
	
	
	public List<AghEspecialidades> pesquisaespAgrupaLoteExame(String parametro) {
		return  this.returnSGWithCount(cadastrosBasicosInternacaoFacade.pesquisarEspecialidadePorSiglaNome(parametro),pesquisaespAgrupaLoteExameCount(parametro));
	}
	
	public Long pesquisaespAgrupaLoteExameCount(String parametro) {
		return cadastrosBasicosInternacaoFacade.pesquisarEspecialidadePorSiglaNomeCount(parametro);
	}
	
	public List<AghEspecialidades> pesquisaEspGenerica(String parametro) {
		return  this.returnSGWithCount(cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeGenerica((parametro != null ? parametro : null)),pesquisaEspGenericaCount(parametro));
	}
	
	public Long pesquisaEspGenericaCount(String parametro) {
		return cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeGenericaCount((parametro != null ? parametro : null));
	}
	
	public List<FccCentroCustos> pesquisaCentroCusto(String parametro) {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		
		return  this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(filtro),pesquisaCentroCustoCount(parametro));
	}

	public Long pesquisaCentroCustoCount(String filtro) {
		return centroCustoFacade.pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(filtro != null ? filtro : null);
	}

	public void buscaChefia() {
		if (especialidade.getCentroCusto() != null) {
			this.especialidade.setServidorChefe(especialidade.getCentroCusto().getRapServidor());
		} else {
			this.especialidade.setServidorChefe(null);
		}
	}
	
	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return  this.returnSGWithCount(this.aghuFacade.pesquisarClinicas(paramPesquisa == null ? null : paramPesquisa),pesquisarClinicasHQLCount(paramPesquisa));
	}

	public Integer pesquisarClinicasHQLCount(String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicasHQLCount(paramPesquisa == null ? null : paramPesquisa);
	}

	private void associaCaracteristicas() {
		for (CaracteristicaEspecialidadeFlag cef : this.caracteristicas) {
			if (cef.getValor()) {
				this.especialidade.associaCaracteristica(cef.getCaracteristica());
			} else {
				this.especialidade.removeCaracteristica(cef.getCaracteristica());
			}
		}		
		mapCaracteristicas.clear();
	}
	
	public void removerCaracEspecialidade(AghCaractEspecialidades caracteristica){
		this.listaCaracEspecialidade.remove(caracteristica);
	}
		
	public String confirmar() {
		
		try {
			associaCaracteristicas();
			associaUsuarioLogado();
			transformarTextosCaixaAlta();
			boolean create = this.especialidade.getSeq() == null;
			boolean agenda = this.especialidade.getEspecialidade() != null;
			
			this.cadastrosBasicosInternacaoFacade.persistirEspecialidade(especialidade);
			
			String msg = null;
			if (create && agenda) {
				msg = "MENSAGEM_SUCESSO_CRIACAO_AGENDA";
			} else if (create) {
				msg = "MENSAGEM_SUCESSO_CRIACAO_ESPECIALIDADE";
			} else if (agenda) {
				msg = "MENSAGEM_SUCESSO_EDICAO_AGENDA";
			} else {
				msg = "MENSAGEM_SUCESSO_EDICAO_ESPECIALIDADE";
			}
			
			apresentarMsgNegocio(Severity.INFO, msg, this.especialidade.getNomeEspecialidade());
			limparCampos();
		} catch (ApplicationBusinessException e) {
//			limparCampos();
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_PESQUISAR_ESPECIALIDADE;
	}
	
	private void associaUsuarioLogado() {
		try {
			RapServidores servidor = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
			this.especialidade.setServidor(servidor);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void transformarTextosCaixaAlta() {
		String nome = this.especialidade.getNomeEspecialidade();
		String nomeUpper = nome.toUpperCase();
		String sigla = this.especialidade.getSigla();
		String siglaUpper = sigla.toUpperCase();
		String nomeReduzido = this.especialidade.getNomeReduzido();
		String nomeReduzidoUpper = nomeReduzido.toUpperCase();
		if (!nome.equals(nomeUpper)) { 
			this.especialidade.setNomeEspecialidade(nomeUpper);
		}
		if (!sigla.equals(siglaUpper)) { 
			this.especialidade.setSigla(siglaUpper);
		}
		if (!nomeReduzido.equals(nomeReduzidoUpper)) { 
			this.especialidade.setNomeReduzido(nomeReduzidoUpper);
		}
	}
	
	public String cancelar() {
		limparCampos();
		return PAGE_PESQUISAR_ESPECIALIDADE;
	}
	
	public void limparCampos(){
		this.especialidade = this.cadastrosBasicosInternacaoFacade.gerarNovaEspecialidade();
		this.mapCaracteristicas.clear();
		this.caracteristicas = null;
	}
	
	/************************* getters and setters  *************************************/ 
	public List<AghCaractEspecialidades> getListCaracEspecialidade() {
		return listaCaracEspecialidade;
	}

	public void setListCaracEspecialidade(
			List<AghCaractEspecialidades> listCaracEspecialidade) {
		this.listaCaracEspecialidade = listCaracEspecialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public List<AghCaractEspecialidades> getListaCaracEspecialidade() {
		return listaCaracEspecialidade;
	}

	public void setListaCaracEspecialidade(
			List<AghCaractEspecialidades> listaCaracEspecialidade) {
		this.listaCaracEspecialidade = listaCaracEspecialidade;
	}
	
	public List<DominioCaracEspecialidade> getListCaracteristicas() {
		return Arrays.asList(DominioCaracEspecialidade.values());
	}
	
	public void setCaracteristicas(List<CaracteristicaEspecialidadeFlag> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	
	public List<CaracteristicaEspecialidadeFlag> getCaracteristicas() {
		return caracteristicas;
	}
}
