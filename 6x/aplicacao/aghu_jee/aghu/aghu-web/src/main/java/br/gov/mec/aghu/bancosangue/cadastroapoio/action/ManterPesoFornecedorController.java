package br.gov.mec.aghu.bancosangue.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedorId;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsFornecedorBolsas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterPesoFornecedorController extends ActionController {

	private static final long serialVersionUID = 8170713827190846458L;

	private static final String PESQUISAR_PESO_FORNECEDOR = "pesquisarPesoFornecedor";

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;	

	private AbsComponentePesoFornecedor absComponentePesoFornecedor;
	
	private AbsComponenteSanguineo componenteSanguineo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
		if (absComponentePesoFornecedor != null && absComponentePesoFornecedor.getId() != null) {
			absComponentePesoFornecedor = bancoDeSangueFacade.obterAbsComponentePesoFornecedorPorChavePrimaria(absComponentePesoFornecedor.getId(),
					true, AbsComponentePesoFornecedor.Fields.COMPONENTE_SANGUINEO, AbsComponentePesoFornecedor.Fields.FORNECEDOR_BOLSA);
			
			if(absComponentePesoFornecedor == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			absComponentePesoFornecedor = new AbsComponentePesoFornecedor();
			absComponentePesoFornecedor.setComponenteSanguineo(componenteSanguineo);
		}
		
		return null;
	}
	
	public List<AbsComponenteSanguineo> pesquisarComponenteSanguineo(String param) {
		return this.returnSGWithCount(bancoDeSangueFacade.obterComponenteSanguineos(param),pesquisarComponenteSanguineoCount(param));
	}
	
	public Long pesquisarComponenteSanguineoCount(String param) {
		return bancoDeSangueFacade.obterComponenteSanguineosCount(param);
	}
	
	public List<AbsFornecedorBolsas> pesquisarFornecedorBolsa(String param) {
		return this.returnSGWithCount(bancoDeSangueFacade.pesquisarFornecedor(param),pesquisarFornecedorBolsaCount(param));  
	}
	
	public Long pesquisarFornecedorBolsaCount(String param) {
		return bancoDeSangueFacade.pesquisarFornecedorCount(param); 
	}
	
	public String gravar() {
		boolean inserindo = false;
			
		try {
			
			if (absComponentePesoFornecedor.getId() == null) {
				inserindo = true;
				AbsComponentePesoFornecedorId id = new AbsComponentePesoFornecedorId(); 
				id.setCsaCodigo(absComponentePesoFornecedor.getComponenteSanguineo().getCodigo());
				id.setFboSeq(absComponentePesoFornecedor.getFornecedorBolsas().getSeq()); 
				absComponentePesoFornecedor.setId(id);
				bancoDeSangueFacade.inserirAbsComponentePesoFornecedor(absComponentePesoFornecedor);
				apresentarMsgNegocio(Severity.INFO,"PESO_FORNECEDOR_MENSAGEM_SUCESSO_GRAVAR_PESO_FORNECEDOR");
				
			} else {
				bancoDeSangueFacade.atualizarAbsComponentePesoFornecedor(absComponentePesoFornecedor);
				apresentarMsgNegocio(Severity.INFO,"PESO_FORNECEDOR_MENSAGEM_SUCESSO_ATUALIZAR_PESO_FORNECEDOR");
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			if(inserindo){
				absComponentePesoFornecedor.setId(null);
			}
			
			apresentarExcecaoNegocio(e);
			return null;
		} 
	}
	
	public String cancelar() {
		absComponentePesoFornecedor = null; 
		return PESQUISAR_PESO_FORNECEDOR;
	}


	public AbsComponentePesoFornecedor getAbsComponentePesoFornecedor() {
		return absComponentePesoFornecedor;
	}

	public void setAbsComponentePesoFornecedor(AbsComponentePesoFornecedor absComponentePesoFornecedor) {
		this.absComponentePesoFornecedor = absComponentePesoFornecedor;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}
}