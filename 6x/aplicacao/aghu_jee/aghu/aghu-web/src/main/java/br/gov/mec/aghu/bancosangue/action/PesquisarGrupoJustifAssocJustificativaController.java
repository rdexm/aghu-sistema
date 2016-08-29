package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.utils.StringUtil;

public class PesquisarGrupoJustifAssocJustificativaController extends ActionController implements ActionPaginator{
	

	private static final long serialVersionUID = -6059483316185335479L;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	private static final long LENGTH_DESCRICAO = 50;

	private static final String PESQUISAR_JUSTIFICATIVAS_USO_HEMOTERAPICO = "bancodesangue-pesquisarJustificativasUsoHemoterapico";

	private static final String MANTER_GRUPO_JUSTIF_ASSOC_JUSTIFICATIVA = "bancodesangue-manterGrupoJustifAssocJustificativa";
	
	private AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo;
	
	private String codigoComponenteSanguineo;
	
	private String codigoProcedimentoHemoterapico;
	
	private String origemRequisicao;
	
	private String origemCadastro;
	
	@Inject @Paginator
	private DynamicDataModel<AbsGrupoJustificativaComponenteSanguineo> dataModel;;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public  List<AbsGrupoJustificativaComponenteSanguineo> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
	
		Short seq = null;
		if(getGrupoJustificativaComponenteSanguineo() != null) {
			seq = getGrupoJustificativaComponenteSanguineo().getSeq();
		}
		
		return bancoDeSangueFacade.pesquisarAbsGrupoJustificativaComponenteSanguineo(firstResult, maxResult,  orderProperty, asc,
				 																	 seq, getGrupoJustificativaComponenteSanguineo().getDescricao(), 
				 																	 getGrupoJustificativaComponenteSanguineo().getSituacao(), 
				 																	 getGrupoJustificativaComponenteSanguineo().getTitulo());
	
	}

	@Override
	public Long recuperarCount() {
		Short seq = null;
		if(getGrupoJustificativaComponenteSanguineo() != null) {
			seq = getGrupoJustificativaComponenteSanguineo().getSeq();
		}
			
		return bancoDeSangueFacade.pesquisarAbsGrupoJustificativaComponenteSanguineoCount(
				seq, getGrupoJustificativaComponenteSanguineo().getDescricao(), getGrupoJustificativaComponenteSanguineo().getSituacao(), getGrupoJustificativaComponenteSanguineo().getTitulo());
		
	}
	
	public String editar(){
		return MANTER_GRUPO_JUSTIF_ASSOC_JUSTIFICATIVA;
	}

	public String inserir() {
		return MANTER_GRUPO_JUSTIF_ASSOC_JUSTIFICATIVA;
	}
	
	public String voltar(){
		return PESQUISAR_JUSTIFICATIVAS_USO_HEMOTERAPICO;
	}
	
	public Boolean isAbreviar(String str){
		Boolean abreviar = Boolean.FALSE;
		if (str != null) {
			abreviar = str.length() > LENGTH_DESCRICAO;
		}
		return abreviar;
	}
	
	public String truncDescricao(String original) {
		String retorno = original;
		if (isAbreviar(original)) {
			retorno = StringUtil.trunc(original, Boolean.TRUE, LENGTH_DESCRICAO);
		}
		return retorno;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void iniciar() {
        if(StringUtils.isNotBlank(getOrigemCadastro())) {
        	pesquisar();
        }
	}
	
	
	public void limpar() {
		grupoJustificativaComponenteSanguineo = new AbsGrupoJustificativaComponenteSanguineo();
		dataModel.limparPesquisa();
	}
	
	public AbsGrupoJustificativaComponenteSanguineo getGrupoJustificativaComponenteSanguineo() {
		if(grupoJustificativaComponenteSanguineo == null){
			grupoJustificativaComponenteSanguineo = new AbsGrupoJustificativaComponenteSanguineo();
		}
		return grupoJustificativaComponenteSanguineo;
	}

	public void setGrupoJustificativaComponenteSanguineo(
			AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo) {
		this.grupoJustificativaComponenteSanguineo = grupoJustificativaComponenteSanguineo;
	}

	public String getCodigoComponenteSanguineo() {
		return codigoComponenteSanguineo;
	}

	public void setCodigoComponenteSanguineo(String codigoComponenteSanguineo) {
		this.codigoComponenteSanguineo = codigoComponenteSanguineo;
	}

	public String getCodigoProcedimentoHemoterapico() {
		return codigoProcedimentoHemoterapico;
	}

	public void setCodigoProcedimentoHemoterapico(String codigoProcedimentoHemoterapico) {
		this.codigoProcedimentoHemoterapico = codigoProcedimentoHemoterapico;
	}

	public String getOrigemRequisicao() {
		return origemRequisicao;
	}

	public void setOrigemRequisicao(String origemRequisicao) {
		this.origemRequisicao = origemRequisicao;
	}

	public String getOrigemCadastro() {
		return origemCadastro;
	}

	public void setOrigemCadastro(String origemCadastro) {
		this.origemCadastro = origemCadastro;
	}

	public DynamicDataModel<AbsGrupoJustificativaComponenteSanguineo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AbsGrupoJustificativaComponenteSanguineo> dataModel) {
		this.dataModel = dataModel;
	}

}