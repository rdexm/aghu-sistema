package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;

import org.primefaces.event.SelectEvent;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarConsultoriasInternacaoController extends ActionController {

	private static final long serialVersionUID = -289471189582395257L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;	
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;		
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private RelatorioListaConsultoriaController relatorioListaConsultoriaController;
	
	
	private AghEspecialidades especialidade;
	private LocalPacienteVO unidadeFuncional;
	private DominioTipoSolicitacaoConsultoria tipo;
	private DominioSimNao urgencia;
	private DominioSituacaoConsultoria situacao;
	
	private List<ConsultoriasInternacaoVO> listaConsultoriasInternacao;
	private ConsultoriasInternacaoVO itemSelecionado;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	private String voltarPara;
	private boolean voltarListaPacientes = false;
	
	private static final String PAGE_CONSULTAR_RESPOSTAS = "consultaRespostasConsultoria";
	
	private static final String RELATORIO_LISTA_CONSULTORIAS = "prescricaomedica-relatorioListaConsultoriaPdf";
	
	private static final String RESPONDER_CONSULTORIA = "prescricaomedica-cadastroRespostasConsultoria";
	
	private static final String PAGE_VISUALIZAR_SOLICITACAO_CONSULTORIA = "visualizaDadosSolicitacaoConsultoria";
	
	private static final String PAGE_LISTA_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
		
		if (especialidade == null) {
			this.situacao = DominioSituacaoConsultoria.P;
			
			this.especialidade = this.prescricaoMedicaFacade.obterEspecialidadePorUsuarioConsultor();
			if (this.especialidade != null) {
				this.pesquisar();
			}
		}
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadesPorNomeOuSigla(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadesPorNomeOuSigla(parametro),
				this.aghuFacade.pesquisarEspecialidadesPorNomeOuSiglaCount(parametro));
	}
	
	public List<LocalPacienteVO> pesquisarUnidFuncionalPorCaracteristica(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidFuncionalPorCaracteristica(parametro),
				this.aghuFacade.pesquisarUnidFuncionalPorCaracteristicaCount(parametro));
	}
	
	public void incluirListaPacientes() {
		if(itemSelecionado == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CONSULTORIA_NAO_SELECIONADA");
			return;
		}
		
		MpmPacAtendProfissional mpmPacAtendProfissional = new MpmPacAtendProfissional(); 
		mpmPacAtendProfissional.setServidor(servidorLogadoFacade.obterServidorLogado());
		mpmPacAtendProfissional.setAtendimento(ambulatorioFacade.obterAtendimentoPorSeq(itemSelecionado.getAtdSeq()));
		try {
			this.prescricaoMedicaFacade.persistirPacAtendProfissional(mpmPacAtendProfissional, null);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONSULTORIA_PACIENTE_ADICIONADO_LISTA");
		} catch (EJBTransactionRolledbackException | ApplicationBusinessException e) {
			try {
				if(prescricaoMedicaFacade.verificaMpmPacAtendProfissionalCadastrado(mpmPacAtendProfissional)){
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONSULTORIA_PACIENTE_ADICIONADO_LISTA");
				}else{
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CONSULTORIA_PACIENTE_ERRO_ADICIONAR");
				}
			} catch (ApplicationBusinessException e1) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CONSULTORIA_PACIENTE_ERRO_ADICIONAR");
			}
		}
	}

	public void pesquisar() {
		this.listaConsultoriasInternacao = null;
		try {
			this.prescricaoMedicaFacade.verificarAcessoProfissionalEspecialidade(this.especialidade.getSeq());
			
			Short unfSeq = this.unidadeFuncional != null ? this.unidadeFuncional.getSeq() : null;
			
			this.relatorioListaConsultoriaController.setSituacaoFiltro(situacao);
			
			this.listaConsultoriasInternacao = this.prescricaoMedicaFacade.listarConsultoriasInternacaoPorAtendimento(this.especialidade.getSeq(),
					unfSeq, this.tipo, this.urgencia, this.situacao);
			
			if (!this.listaConsultoriasInternacao.isEmpty()) {
				
				this.itemSelecionado = this.listaConsultoriasInternacao.get(0);
				MpmPrescricaoMedica pm = new MpmPrescricaoMedica(
						new MpmPrescricaoMedicaId(this.itemSelecionado.getAtdSeq(), this.itemSelecionado.getSeq()));
				PrescricaoMedicaVO vo = new PrescricaoMedicaVO();
				vo.setPrescricaoMedica(pm);
				
				this.prescricaoMedicaVO = vo;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarItem(SelectEvent e) {
		this.itemSelecionado = (ConsultoriasInternacaoVO) e.getObject();
		MpmPrescricaoMedica pm = new MpmPrescricaoMedica(
				new MpmPrescricaoMedicaId(this.itemSelecionado.getAtdSeq(), this.itemSelecionado.getSeq()));
		PrescricaoMedicaVO vo = new PrescricaoMedicaVO();
		vo.setPrescricaoMedica(pm);
		
		this.prescricaoMedicaVO = vo;
	}
	
	public void limparCampos() {
		this.especialidade = null;
		this.tipo = null;
		this.unidadeFuncional = null;
		this.urgencia = null;
		this.prescricaoMedicaVO = null;
		this.listaConsultoriasInternacao = null;
		this.situacao = DominioSituacaoConsultoria.P;
	}
	
	public String responderConsultoria() {
		return RESPONDER_CONSULTORIA;
	}
	
	
	public String redirecionarConsultarRespostas() {
		return PAGE_CONSULTAR_RESPOSTAS;
	}
	
	public String visualizarRelatorio() {
		return RELATORIO_LISTA_CONSULTORIAS;
	}
	
	public String redirecionarVisualizarSolicitacao() {
		return PAGE_VISUALIZAR_SOLICITACAO_CONSULTORIA;
	}
	
	public String redirecionarListaPacientesInternados(){
		this.voltarListaPacientes = false;
		return PAGE_LISTA_PACIENTES_INTERNADOS;
	}

	// getters & setters
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public LocalPacienteVO getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(LocalPacienteVO unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioTipoSolicitacaoConsultoria getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoSolicitacaoConsultoria tipo) {
		this.tipo = tipo;
	}

	public DominioSimNao getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(DominioSimNao urgencia) {
		this.urgencia = urgencia;
	}

	public DominioSituacaoConsultoria getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConsultoria situacao) {
		this.situacao = situacao;
	}

	public List<ConsultoriasInternacaoVO> getListaConsultoriasInternacao() {
		return listaConsultoriasInternacao;
	}

	public void setListaConsultoriasInternacao(
			List<ConsultoriasInternacaoVO> listaConsultoriasInternacao) {
		this.listaConsultoriasInternacao = listaConsultoriasInternacao;
	}

	public ConsultoriasInternacaoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ConsultoriasInternacaoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isVoltarListaPacientes() {
		return voltarListaPacientes;
	}

	public void setVoltarListaPacientes(boolean voltarListaPacientes) {
		this.voltarListaPacientes = voltarListaPacientes;
	}
}
