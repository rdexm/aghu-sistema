package br.gov.mec.aghu.prescricaomedica.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MpmAltaSumario;

public class CadastroAtestadoAcompanhamentoController extends ActionController {

	private static final long serialVersionUID = -6825993351114580683L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
//	@Inject
//	private RelatorioAtestadosController relatorioAtestadosController;

	// Recebido por parâmetro
	private MpmAltaSumario mpmAltaSumario;

	private MamAtestados mamAtestado;
	private MamAtestados itemSelecionado;
	// Workaround para funcionar o atributo selection da tabela.
	private MamAtestados itemAux;
	private List<MamAtestados> mamAtestados;
	private boolean modoEdicao;
	private String infoPaciente;
	private AghParametros pTipoAtestado;
	private Integer atdSeq;
	
	/** #46218 */
	private String listaOrigem;
	private final static String ATESTADO = "ATESTADO";
	
	private final static String ALTA = "ALTA";
	

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void inicio() {
		this.infoPaciente = mpmAltaSumario.getPaciente().getNome().concat(getBundle().getString("MENSAGEM_ATESTADO_ACOMP_PAC_1"))
				.concat(mpmAltaSumario.getPaciente().getProntuario().toString()).concat(getBundle().getString("MENSAGEM_ATESTADO_ACOMP_PAC_2"));

		this.recuperaParametroAtestadoAcompanhamento();
		this.limpar();
		this.pesquisar();
	}

	private void recuperaParametroAtestadoAcompanhamento() {
		try {
			pTipoAtestado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ATESTADO_ACOMPANHAMENTO_INTERNACAO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		if(this.listaOrigem != null && this.listaOrigem.trim().equals(ATESTADO)){
			this.mamAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipo(atdSeq, 
					pTipoAtestado.getVlrNumerico().shortValue(),null);
		}
		else if(listaOrigem !=null && this.listaOrigem.trim().equals(ALTA)){
			this.mamAtestados = this.ambulatorioFacade.listarAtestadosPorPacienteTipo(null, 
					pTipoAtestado.getVlrNumerico().shortValue(),mpmAltaSumario);
		}
	}

	public void gravar() {

		try {

			this.ambulatorioFacade.validarDatasAtestado(this.mamAtestado.getDataInicial(), this.mamAtestado.getDataFinal());
			/** #46218 */
			if(this.listaOrigem.trim().equals(ATESTADO)){
				this.mamAtestado.setAltaSumario(null);
				this.mamAtestado.setAtendimento(new AghAtendimentos());
				this.mamAtestado.getAtendimento().setSeq(this.atdSeq);
			}
			else if(listaOrigem !=null && this.listaOrigem.trim().equals(ALTA)) {
				this.mamAtestado.setAltaSumario(this.mpmAltaSumario);
			}
			this.ambulatorioFacade.persistirMamAtestado(this.mamAtestado, false);

			if (this.modoEdicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_ALTERACAO_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_INCLUSAO_SUCESSO");
			}
			this.modoEdicao = Boolean.FALSE;
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editar() {
		this.mamAtestado = this.itemSelecionado;
		this.modoEdicao = Boolean.TRUE;
	}

	public void excluir() {
		try {
			this.ambulatorioFacade.excluirAtestadoAcompanhamento(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATESTADO_EXCLUSAO_SUCESSO");
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao() {
		this.modoEdicao = Boolean.FALSE;
		this.limpar();
	}

	public void limpar() {
		this.mamAtestado = new MamAtestados();
		this.mamAtestado.setDthrCriacao(new Date());
		this.mamAtestado.setAipPacientes(this.mpmAltaSumario.getPaciente());
		this.mamAtestado.setMamTipoAtestado(this.ambulatorioFacade.obterTipoAtestadoPorSeq(pTipoAtestado.getVlrNumerico().shortValue()));
		this.mamAtestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
		this.mamAtestado.setIndImpresso(Boolean.TRUE);
		this.mamAtestado.setAltaSumario(this.mpmAltaSumario);
		this.mamAtestado.setNroVias(Byte.valueOf("1"));
	}
	
	/**
	 * #46252 - Chama a impressão do relatório #46485
	 */
	public void imprimirAtestado() {
//		if (this.itemSelecionado != null) {
//			try {
//				AtestadoVO atestado = this.prescricaoMedicaFacade
//						.obterDocumentoPacienteAtestado(this.itemSelecionado.getSeq(), false);
//				
//				this.relatorioAtestadosController.setAtestado(atestado);
//				this.relatorioAtestadosController.setDescricaoDocumento("Acompanhamento");
//				this.relatorioAtestadosController.directPrint();
//			} catch (ApplicationBusinessException e) {
//				apresentarExcecaoNegocio(e);
//			}
//		} else {
//			apresentarMsgNegocio(Severity.INFO, "MAM_SELECIONE_ATESTADO_IMPRIMIR");
//		}
	}

	// Getters and setters
	public MpmAltaSumario getMpmAltaSumario() {
		return mpmAltaSumario;
	}

	public void setMpmAltaSumario(MpmAltaSumario mpmAltaSumario) {
		this.mpmAltaSumario = mpmAltaSumario;
	}

	public MamAtestados getMamAtestado() {
		return mamAtestado;
	}

	public void setMamAtestado(MamAtestados mamAtestado) {
		this.mamAtestado = mamAtestado;
	}

	public MamAtestados getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MamAtestados itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public MamAtestados getItemAux() {
		return itemAux;
	}

	public void setItemAux(MamAtestados itemAux) {
		this.itemAux = itemAux;
	}

	public List<MamAtestados> getMamAtestados() {
		return mamAtestados;
	}

	public void setMamAtestados(List<MamAtestados> mamAtestados) {
		this.mamAtestados = mamAtestados;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public String getInfoPaciente() {
		return infoPaciente;
	}

	public void setInfoPaciente(String infoPaciente) {
		this.infoPaciente = infoPaciente;
	}

	public String getListaOrigem() {
		return listaOrigem;
	}

	public void setListaOrigem(String listaOrigem) {
		this.listaOrigem = listaOrigem;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
}
