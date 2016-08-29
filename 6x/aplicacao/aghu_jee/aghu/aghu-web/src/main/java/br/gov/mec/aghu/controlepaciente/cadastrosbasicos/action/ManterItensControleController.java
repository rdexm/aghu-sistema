package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterItensControleController extends ActionController {

	private static final long serialVersionUID = -5902648858034984706L;
	
	private static final String PESQUISAR_ITENS_CONTROLE = "pesquisarItensControle";
	private static final Enum[] fetchArgsInnerJoin = {EcpItemControle.Fields.GRUPO};
	private static final Enum[] fetchArgsLeftJoin = {EcpItemControle.Fields.UNIDADE_MEDIDA_MEDICA};
	
	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	private EcpItemControle itemControle;
	
	private boolean emEdicao = false;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (itemControle != null && itemControle.getSeq() != null) {
			emEdicao = true;
			itemControle = cadastrosBasicosControlePacienteFacade.obterItemControle(itemControle.getSeq(), fetchArgsInnerJoin, fetchArgsLeftJoin);
			
			if(itemControle == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
		} else {
			this.itemControle = new EcpItemControle();
			itemControle.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String salvar() {
		try {
			if(emEdicao){
				this.cadastrosBasicosControlePacienteFacade.alterar(itemControle);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_ITEM_CONTROLE", itemControle.getDescricao());
				
			} else {
				cadastrosBasicosControlePacienteFacade.inserir(itemControle);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_ITEM_CONTROLE", itemControle.getDescricao());
			}
	
			return voltar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	} 

	public List<EcpGrupoControle> getListaGruposControleAtivos() {
		return cadastrosBasicosControlePacienteFacade.listarGruposControleAtivos();
	}
	
	public List<MpmUnidadeMedidaMedica> obterUnidadesMedida(String parametroConsulta) {
		return this.cadastrosBasicosPrescricaoMedicaFacade.listarUnidadesMedidaMedicaAtivas(parametroConsulta);
	}

	public List<MpmUnidadeMedidaMedica> obterUnidadesMedida() {
		return this.cadastrosBasicosPrescricaoMedicaFacade.listarUnidadesMedidaMedicaAtivas();
	}
	
	public void limpar() {
		this.itemControle = null;
		this.emEdicao = false;
	}

	public String voltar() {
		this.limpar();
		return PESQUISAR_ITENS_CONTROLE ;
	}

	public EcpItemControle getItemControle() {
		return itemControle;
	}

	public void setItemControle(EcpItemControle itemControle) {
		this.itemControle = itemControle;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	} 
}