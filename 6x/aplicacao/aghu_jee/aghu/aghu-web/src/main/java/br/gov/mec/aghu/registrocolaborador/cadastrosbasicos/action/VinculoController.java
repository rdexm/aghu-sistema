package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class VinculoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	//private static final Log LOG = LogFactory.getLog(VinculoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 682707467336265591L;

	private static final String PESQUISAR_VINCULO = "pesquisarVinculo.xhtml";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;	

	
	private RapVinculos vinculo = new RapVinculos();

	private Integer vinCodigo;
	private Short codigoVinculo;
	
	private Boolean mostraCampoMatricula;

	private void limparCadastro() {
		this.vinculo = new RapVinculos();
		this.vinCodigo = null;
		this.codigoVinculo = null;
	}

	public String cancelarCadastro() {
		this.limparCadastro();
		return PESQUISAR_VINCULO;
	}

	public void iniciar() {

		if (this.codigoVinculo != null) {
			try {
				vinculo = cadastrosBasicosFacade.obterVinculo(this.codigoVinculo);
				
				if(vinculo == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return;
				}
				
				vinCodigo = vinculo.getCodigo().intValue();
				if (vinculo.getIndGeraMatricula() == DominioSimNao.S){
					this.setMostraCampoMatricula(true);
				}
			} catch (ApplicationBusinessException ex) {
				apresentarExcecaoNegocio(ex);
			}
		} else {
			this.vinCodigo = null;
			this.vinculo = new RapVinculos();
			this.vinculo.setIndSituacao(DominioSituacao.A);
			this.vinculo.setIndTransferencia(DominioSimNao.S);
			this.vinculo.setIndDependente(DominioSimNao.N);
			this.vinculo.setIndGeraMatricula(DominioSimNao.N);
			this.vinculo.setIndCcustLotacao(DominioSimNao.N);
			this.vinculo.setIndOcupacao(DominioSimNao.N);
			this.vinculo.setIndExigeTermino(DominioSimNao.N);
			this.vinculo.setIndGestaoDesempenho(DominioSimNao.N);
			this.vinculo.setIndEmiteContrato(DominioSimNao.N);
			this.vinculo.setIndPermDuplic(DominioSimNao.N);
			this.vinculo.setIndExigeCpfRg(DominioSimNao.S);
			this.setMostraCampoMatricula(false);
		}
	}

	public String salvar() throws ApplicationBusinessException {
		String retorno = null;
		try {
			boolean edicao = this.codigoVinculo != null;
			vinculo.setCodigo(vinCodigo.shortValue());

			if (edicao) {
				cadastrosBasicosFacade.alterar(vinculo);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VINCULO_ALTERADO_COM_SUCESSO");
			} else {
				cadastrosBasicosFacade.salvar(vinculo);
				cadastrosBasicosFacade.enviaEmail(vinculo.getCodigo(), vinculo.getDescricao(), vinculo.getIndGeraMatricula());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VINCULO_INCLUIDO_COM_SUCESSO");
			}
			limparCadastro();
			retorno = PESQUISAR_VINCULO;
		} catch (ApplicationBusinessException ex) {
			apresentarExcecaoNegocio(ex);
		}
		return retorno;
	}
	
	public void trocaIndGeraMatricula () throws ApplicationBusinessException{
		if (vinculo.getIndGeraMatricula() == DominioSimNao.S){
			this.setMostraCampoMatricula(true);
			
			Integer proximoCod = registroColaboradorFacade.obterProximoCodStarhLivre();
			if (proximoCod == null){
				cadastrosBasicosFacade.populaTabela();
				proximoCod = registroColaboradorFacade.obterProximoCodStarhLivre();
			}
			this.vinculo.setMatriculaNova(proximoCod);
		} else {
			this.setMostraCampoMatricula(false);
			this.vinculo.setMatriculaNova(null);
		}
	}

	public Boolean getMostraCampoMatricula() {
		return mostraCampoMatricula;
	}

	public void setMostraCampoMatricula(Boolean mostraCampoMatricula) {
		this.mostraCampoMatricula = mostraCampoMatricula;
	}
	
	public RapVinculos getVinculo() {
		return vinculo;
	}

	public void setVinculo(RapVinculos vinculo) {
		this.vinculo = vinculo;
	}

	public Short getCodigoVinculo() {
		return codigoVinculo;
	}

	public void setCodigoVinculo(Short codigoVinculo) {
		this.codigoVinculo = codigoVinculo;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
}
