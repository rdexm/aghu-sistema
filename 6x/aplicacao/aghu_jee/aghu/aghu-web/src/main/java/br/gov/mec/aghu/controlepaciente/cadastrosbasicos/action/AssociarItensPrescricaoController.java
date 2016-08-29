package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class AssociarItensPrescricaoController extends ActionController {

	private static final long serialVersionUID = 3761250997877683144L;

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	private List<EpeCuidados> listaItensCuidadoEnfermagem;
	private List<EpeCuidados> listaItensCuidadoEnfermagemOrigem = new ArrayList<EpeCuidados>();
	private List<MpmCuidadoUsual> listaItensCuidadoMedico;
	private List<MpmCuidadoUsual> listaItensCuidadoMedicoOrigem = new ArrayList<MpmCuidadoUsual>();
	private EcpItemControle itemControle;
	private EpeCuidados cuidado;
	private EpeCuidados cuidadoSelecionado;
	private MpmCuidadoUsual cuidadoUsual;
	private MpmCuidadoUsual cuidadoUsualSelecionado;
	private boolean alteracaoPendente = false;

	private static CuidadoEnfermagemComparator cuidadoEnfermagemComparator = new CuidadoEnfermagemComparator();
	private static CuidadoMedicoComparator cuidadoMedicoComparator = new CuidadoMedicoComparator();
	
	private static final String PESQUISAR_ITENS_CONTROLE = "pesquisarItensControle";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Carregar o item de controle recebido por parâmetro e buscar os cuidados
	 * médicos e de enfermagem vinculados ao item. Além disso, guarda a lista
	 * original (médico/enfermagem) para posterior avaliação do que foi incluído e excluído em tela
	 */
	public void iniciar() {
	 

	 
		
		
		// carregar a lista de itens com cuidados de enfermagem
		listaItensCuidadoEnfermagem = this.cadastrosBasicosControlePacienteFacade.obterItensCuidadoEnfermagem(itemControle);

		listaItensCuidadoEnfermagemOrigem.clear();
		listaItensCuidadoEnfermagemOrigem.addAll(listaItensCuidadoEnfermagem);

		// carregar a lista de itens com cuidados médicos
		listaItensCuidadoMedico = this.cadastrosBasicosControlePacienteFacade.obterItensCuidadoMedico(itemControle);

		listaItensCuidadoMedicoOrigem.clear();
		listaItensCuidadoMedicoOrigem.addAll(listaItensCuidadoMedico);
	
	}
	

	/**
	 * Pesquisar os cuidados de enfermagem através da suggestionBox
	 */
	public List<EpeCuidados> pesquisarCuidadoEnfermagem(String parametro) {
		String paramString = (String) parametro;

		Set<EpeCuidados> result = new HashSet<EpeCuidados>();
		if ((cuidadoSelecionado == null) || 
				!(StringUtils.equalsIgnoreCase(paramString,String.valueOf(cuidadoSelecionado.getSeq())) || 
						StringUtils.equalsIgnoreCase(paramString,cuidadoSelecionado.getDescricao()))) {
			
			result = new HashSet<EpeCuidados>(prescricaoEnfermagemFacade.pesquisarCuidadosEnfermagem(paramString));
			
		} else {
			result.add(cuidadoSelecionado);
		}
		
		List<EpeCuidados> resultReturn = new ArrayList<EpeCuidados>(result);
		Collections.sort(resultReturn, cuidadoEnfermagemComparator);
		return resultReturn;
	}
	
	/**
	 * Setar o cuidado de enfermagem selecionado pelo usuário
	 */
	public void selecionouCuidadoEnfermagem() {
		setCuidadoSelecionado(getCuidado());
	}

	/**
	 * Adiciona um cuidado de enfermagem na lista em memória.<br>
	 * Não permite incluir cuidados que já estejam na lista.
	 */
	public void adicionarCuidadoEnfermagem() {
		if (this.getCuidadoSelecionado() != null) {
			if (!listaItensCuidadoEnfermagem.contains(getCuidadoSelecionado())) {
				listaItensCuidadoEnfermagem.add(getCuidadoSelecionado());
				Collections.sort(listaItensCuidadoEnfermagem,cuidadoEnfermagemComparator);
				this.setCuidado(null);
				this.setCuidadoSelecionado(null);
				this.setAlteracaoPendente(true);
			} else {
				apresentarMsgNegocio(Severity.WARN,"MENSAGEM_CONFIG_LISTA_CUIDADO_ENFERMAGEM_JA_ADICIONADO");
			}
		}
	}
	
	/**
	 * Método que exclui um cuidado de enfermagem da lista em memória. Ignora nulos
	 */
	public void excluirCuidadoEnfermagem(EpeCuidados cuidado) {		
		if (cuidado != null) {
			listaItensCuidadoEnfermagem.remove(cuidado);
			this.setAlteracaoPendente(true);
		}
	}
	

	/**
	 * Verifica quais cuidados (enfermagem/médico) deverão ser incluídos e
	 * excluídos do vínculo com o item de controle. Após esta verificação,
	 * realiza a persistência dos dados.
	 */
	@SuppressWarnings("unchecked")
	public String salvar() {

		//Obtem os cuidados de enfermagem incluídos e excluídos
		List<EpeCuidados> cuidadosEnfermagemIncluir = ListUtils.subtract(listaItensCuidadoEnfermagem, listaItensCuidadoEnfermagemOrigem);
		List<EpeCuidados> cuidadosEnfermagemExcluir = ListUtils.subtract(listaItensCuidadoEnfermagemOrigem, listaItensCuidadoEnfermagem);
		
		try {
			// Realiza a gravação dos cuidados de enfermagem
			this.cadastrosBasicosControlePacienteFacade.salvarAssociacaoCuidadosEnfermagem(itemControle, cuidadosEnfermagemIncluir, cuidadosEnfermagemExcluir);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		//Obtem os cuidados médicos incluídos e excluídos
		List<MpmCuidadoUsual> cuidadosMedicosIncluir = ListUtils.subtract(listaItensCuidadoMedico, listaItensCuidadoMedicoOrigem);
		List<MpmCuidadoUsual> cuidadosMedicosExcluir = ListUtils.subtract(listaItensCuidadoMedicoOrigem, listaItensCuidadoMedico);

		try {
			// Realiza a gravação dos cuidados médicos
			this.cadastrosBasicosControlePacienteFacade.salvarAssociacaoCuidadosMedicos(itemControle, cuidadosMedicosIncluir, cuidadosMedicosExcluir);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ASSOCIACAO_REALIZADA_COM_SUCESSO");
		
		return voltar();
	}

	public String verificarAlteracoesPendentes() {
		if (this.isAlteracaoPendente()) {
			openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}
		return voltar();
	}
	
	/**
	 * Pesquisar os cuidados médicos através da suggestionBox
	 */
	public List<MpmCuidadoUsual> pesquisarCuidadoMedico(String parametro) {
		String paramString = (String) parametro;

		Set<MpmCuidadoUsual> result = new HashSet<MpmCuidadoUsual>();
		if ((cuidadoUsualSelecionado == null) || 
				!(StringUtils.equalsIgnoreCase(paramString, String.valueOf(cuidadoUsualSelecionado.getSeq())) || 
						StringUtils.equalsIgnoreCase(paramString,cuidadoUsualSelecionado.getDescricao()))) {
			
			result = new HashSet<MpmCuidadoUsual>(cadastrosBasicosPrescricaoMedicaFacade.pesquisarCuidadosMedicos(paramString));
			
		} else {
			result.add(cuidadoUsualSelecionado);
		}
		
		List<MpmCuidadoUsual> resultReturn = new ArrayList<MpmCuidadoUsual>(result);
		Collections.sort(resultReturn, cuidadoMedicoComparator);
		return resultReturn;
	}

	/**
	 * Setar o cuidado médico selecionado pelo usuário
	 */
	public void selecionouCuidadoMedico() {
		setCuidadoUsualSelecionado(getCuidadoUsual());
	}

	/**
	 * Adiciona um cuidado médico na lista em memória.<br>
	 * Não permite incluir cuidados que já estejam na lista.
	 */
	public void adicionarCuidadoMedico() {
		if (this.getCuidadoUsualSelecionado() != null) {
			if (!listaItensCuidadoMedico.contains(getCuidadoUsualSelecionado())) {
				listaItensCuidadoMedico.add(getCuidadoUsualSelecionado());
				Collections.sort(listaItensCuidadoMedico, cuidadoMedicoComparator);
				this.setCuidadoUsual(null);
				this.setCuidadoUsualSelecionado(null);
				this.setAlteracaoPendente(true);
			} else {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONFIG_LISTA_CUIDADO_MEDICO_JA_ADICIONADO");
			}
		}
	}

	/**
	 * Método que exclui um cuidado médico da lista em memória. Ignora nulos
	 */
	public void excluirCuidadoMedico(MpmCuidadoUsual cuidado) {		
		if (cuidado != null) {
			listaItensCuidadoMedico.remove(cuidado);
			this.setAlteracaoPendente(true);
		}
	}
	
	public String voltar(){
		limpar();
		return PESQUISAR_ITENS_CONTROLE;
	}

	/**
	 * Retorna os valores da tela ao conteúdo do banco de dados
	 */
	public void limpar() {
		this.iniciar();
		this.setCuidado(null);
		this.setCuidadoUsual(null);
		this.setAlteracaoPendente(false);
	}

	public List<EpeCuidados> getListaItensCuidadoEnfermagem() {
		return listaItensCuidadoEnfermagem;
	}

	public void setListaItensCuidadoEnfermagem(
			List<EpeCuidados> listaItensCuidadoEnfermagem) {
		this.listaItensCuidadoEnfermagem = listaItensCuidadoEnfermagem;
	}

	public List<MpmCuidadoUsual> getListaItensCuidadoMedico() {
		return listaItensCuidadoMedico;
	}

	public void setListaItensCuidadoMedico(
			List<MpmCuidadoUsual> listaItensCuidadoMedico) {
		this.listaItensCuidadoMedico = listaItensCuidadoMedico;
	}

	public EcpItemControle getItemControle() {
		return itemControle;
	}

	public void setItemControle(EcpItemControle itemControle) {
		this.itemControle = itemControle;
	}

	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
	}

	public EpeCuidados getCuidadoSelecionado() {
		return cuidadoSelecionado;
	}

	public void setCuidadoSelecionado(EpeCuidados cuidadoSelecionado) {
		this.cuidadoSelecionado = cuidadoSelecionado;
	}

	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}

	public MpmCuidadoUsual getCuidadoUsualSelecionado() {
		return cuidadoUsualSelecionado;
	}

	public void setCuidadoUsualSelecionado(
			MpmCuidadoUsual cuidadoUsualSelecionado) {
		this.cuidadoUsualSelecionado = cuidadoUsualSelecionado;
	}	
	
	public boolean isAlteracaoPendente() {
		return alteracaoPendente;
	}

	public void setAlteracaoPendente(boolean alteracaoPendente) {
		this.alteracaoPendente = alteracaoPendente;
	}

	private static class CuidadoEnfermagemComparator implements Comparator<EpeCuidados> {
		@Override
		public int compare(EpeCuidados e1, EpeCuidados e2) {
			return e1.getDescricao().compareToIgnoreCase(e2.getDescricao());
		}
	}

	private static class CuidadoMedicoComparator implements Comparator<MpmCuidadoUsual> {
		@Override
		public int compare(MpmCuidadoUsual o1, MpmCuidadoUsual o2) {
			return o1.getDescricao().compareToIgnoreCase(o2.getDescricao());
		}
	}
}