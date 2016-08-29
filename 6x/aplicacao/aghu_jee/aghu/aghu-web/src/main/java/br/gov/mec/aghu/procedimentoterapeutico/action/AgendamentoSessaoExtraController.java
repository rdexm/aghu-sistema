package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptSessaoExtra;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiaPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacientePTVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RapServidoresProcedimentoTerapeuticoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

public class AgendamentoSessaoExtraController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5132805369569564963L;
	private static final Log LOG = LogFactory.getLog(AgendamentoSessaoExtraController.class);
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	
	private MptTipoSessao mptTipoSessaoSelecionado;
	private MptSalas mptSalasSelecionado;
	private RapServidores rapServidores;
	
	@Inject
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@Inject
	private IPacienteFacade pacienteFacade;
	
	private Date dataAtual;
	private Date horaInicio;
	private Date horaFim;
	
	private RapServidoresProcedimentoTerapeuticoVO autorizadoPor;
	private RapServidoresProcedimentoTerapeuticoVO solicitadoPor;
	private MptJustificativa mptJustificativa;
	
	private AipPacientes paciente;
	
	private List<MptTipoSessao> listaMptTipoSessao;
	
	private List<MptSalas> listaMptSalas;
	
	private List<PrescricaoPacientePTVO> listaPrescricaoPacientePTVO;
	
	private List<DiaPrescricaoVO> listaDiaPrescricaoVO;
	
	private PrescricaoPacientePTVO prescricaoPacientePTVOSelecionado;
	private PrescricaoPacientePTVO prescricaoPacientePTVO;
	private DiaPrescricaoVO diaPrescricaoVOSelecionado;
	private DiaPrescricaoVO diaPrescricaoVO;
	
	private String complementoJustificativa;
	
	private Integer pacCodigo;
	
	@PostConstruct
	protected void init(){
		try {
			this.inicializar();
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro no @PostConstruct AgendamentoSessaoExtraController", e);
		}
	}
	
	protected void inicializar() throws ApplicationBusinessException{
		this.begin(conversation);
		String login = this.obterLoginUsuarioLogado();
		this.rapServidores = this.registroColaboradorFacade.obterServidorPorUsuario(login);
		MptFavoritoServidor mptFavoritoServidor = 
				this.procedimentoTerapeuticoFacade.obterServidorSelecionadoPorMatriculaVinculo(rapServidores.getId().getMatricula(),
						rapServidores.getId().getVinCodigo());
		if(mptFavoritoServidor != null && mptFavoritoServidor.getTipoSessao() != null){
			mptTipoSessaoSelecionado = mptFavoritoServidor.getTipoSessao();
			this.listaMptSalas = this.procedimentoTerapeuticoFacade.obterListaSalasPorTpsSeq(mptTipoSessaoSelecionado.getSeq());
		}
		if(mptFavoritoServidor != null && mptFavoritoServidor.getSala() != null){
			mptSalasSelecionado = mptFavoritoServidor.getSala();
		}
		
		listaMptTipoSessao = this.procedimentoTerapeuticoFacade.obterListaTipoSessaoPorIndSituacaoAtiva();
		diaPrescricaoVOSelecionado = new DiaPrescricaoVO();
		diaPrescricaoVO = new DiaPrescricaoVO();
		prescricaoPacientePTVO = new PrescricaoPacientePTVO();
		prescricaoPacientePTVOSelecionado = new PrescricaoPacientePTVO();
	}
	
	public void iniciar(){
		dataAtual = new Date();
		if(pacCodigo!=null){
			pesquisarPacientePesquisaFonetica();
		}
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(Integer.valueOf((String) event.getNewValue()), event.getComponent().getId());
			if(paciente!=null){
				listaPrescricaoPacientePTVO = this.procedimentoTerapeuticoFacade.obterListaPrescricaoPacientePTVO(paciente.getCodigo());
				if(listaPrescricaoPacientePTVO != null && !listaPrescricaoPacientePTVO.isEmpty()){
					prescricaoPacientePTVO.setCloSeq(listaPrescricaoPacientePTVO.get(0).getCloSeq());
					prescricaoPacientePTVOSelecionado = listaPrescricaoPacientePTVO.get(0);
					listaDiaPrescricaoVO = this.procedimentoTerapeuticoFacade.obterDiaPrescricaoVO(this.prescricaoPacientePTVOSelecionado.getLote()
							, this.prescricaoPacientePTVOSelecionado.getCloSeq());
				}
			}
			
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisarPacientePesquisaFonetica(){
		
		paciente = pacienteFacade.obterPaciente(this.pacCodigo);
		if(paciente != null){
			this.listaPrescricaoPacientePTVO = this.procedimentoTerapeuticoFacade.obterListaPrescricaoPacientePTVO(paciente.getCodigo());
			if(listaPrescricaoPacientePTVO != null && !this.listaPrescricaoPacientePTVO.isEmpty()){
				prescricaoPacientePTVO = new PrescricaoPacientePTVO();
				prescricaoPacientePTVO.setCloSeq(listaPrescricaoPacientePTVO.get(0).getCloSeq());
				prescricaoPacientePTVOSelecionado = new PrescricaoPacientePTVO();
				prescricaoPacientePTVOSelecionado = listaPrescricaoPacientePTVO.get(0);
				listaDiaPrescricaoVO = this.procedimentoTerapeuticoFacade.obterDiaPrescricaoVO(this.prescricaoPacientePTVOSelecionado.getLote(),
						this.prescricaoPacientePTVOSelecionado.getCloSeq());
				}
			diaPrescricaoVOSelecionado = new DiaPrescricaoVO();
			diaPrescricaoVO = new DiaPrescricaoVO();
		}
	}
	

	public String obterProtocoloPrescricaoVOPorCloSeq(Integer cloSeq) {
		List<ProtocoloPrescricaoVO> listaPrescricaoVO = this.procedimentoTerapeuticoFacade.obterProtocoloPrescricaoVOPorCloSeq(cloSeq);
		String retorno = StringUtils.EMPTY;
		if(listaPrescricaoVO != null && !listaPrescricaoVO.isEmpty()){
			if(listaPrescricaoVO.get(0).getDescricao() != null){
				retorno = listaPrescricaoVO.get(0).getDescricao();
			}else{
				for (ProtocoloPrescricaoVO protocoloPrescricaoVO : listaPrescricaoVO) {
					if(retorno.trim().isEmpty()){
						retorno = protocoloPrescricaoVO.getTitulo();
					}else{
						retorno = retorno.concat(" - ").concat(protocoloPrescricaoVO.getTitulo());
					}
				}
			}
		}
		return retorno;
	}
	
	public String obterTempoAdministracao(Short tempo){
		
		if(tempo != null){
			String horas = String.valueOf(tempo / 60);
			String min = String.valueOf(tempo % 60);
			horas = StringUtil.adicionaZerosAEsquerda(horas, 2);
			return horas.concat(":"+min);
		}else{
			return "00:00";
		}
		
	}
	
	public void atualizarListaSala(){
		if(this.mptTipoSessaoSelecionado != null){
			this.listaMptSalas = this.procedimentoTerapeuticoFacade.obterListaSalasPorTpsSeq(this.mptTipoSessaoSelecionado.getSeq());
		}else{
			this.listaMptSalas = null;
			this.mptSalasSelecionado = null;
		}
	}
	
	public void gravar() throws ParseException{
		try {
			procedimentoTerapeuticoFacade.validarCampoObrigatorioAgendamentoSessaoExtra(mptTipoSessaoSelecionado, this.horaInicio, this.diaPrescricaoVOSelecionado);

			MptAgendamentoSessao mptAgendamentoSessao = new MptAgendamentoSessao();
			mptAgendamentoSessao.setTipoSessao(mptTipoSessaoSelecionado);
			mptAgendamentoSessao.setSala(mptSalasSelecionado);
			mptAgendamentoSessao.setPaciente(paciente);
			mptAgendamentoSessao.setaPartirDe(this.dataAtual);
			mptAgendamentoSessao.setAte(this.dataAtual);
			//NÃO ESTÃO DEFINIDOS EM DOCUMENTAÇÃO
			mptAgendamentoSessao.setServidor(this.rapServidores);
			mptAgendamentoSessao.setCriadoEm(new Date());
			procedimentoTerapeuticoFacade.persistirMptAgendamentoSessao(mptAgendamentoSessao);
			
			MptSessao mptSessao = this.procedimentoTerapeuticoFacade.obterMptSessaoPorChavePrimaria(diaPrescricaoVOSelecionado.getSeq());
			MptHorarioSessao mptHorarioSessao = new MptHorarioSessao();
			mptHorarioSessao.setDia(diaPrescricaoVOSelecionado.getDia());
			
			int horas = 0;
			int minuto = 0;
			
			if (diaPrescricaoVOSelecionado.getTempoAdministracao() != null){
				horas = diaPrescricaoVOSelecionado.getTempoAdministracao() / 60;
				minuto = diaPrescricaoVOSelecionado.getTempoAdministracao() % 60;
			}
			
			Date tempo = DateUtil.obterData(1, 0, 1, horas, minuto);
			mptHorarioSessao.setTempo(tempo);
			mptHorarioSessao.setCiclo(prescricaoPacientePTVOSelecionado.getCiclo());
			
			mptHorarioSessao.setDataInicio(DateUtil.comporDiaHora(this.dataAtual, this.horaInicio));
			mptHorarioSessao.setDataFim(DateUtil.comporDiaHora(this.dataAtual, this.horaFim));
			mptHorarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.E);
			mptHorarioSessao.setMptSessao(mptSessao);
			/*NÃO ESPECIFICADO EM DOCUMENTAÇÃO */
			mptHorarioSessao.setAgendamentoSessao(mptAgendamentoSessao);
			mptHorarioSessao.setCriadoEm(new Date());
			mptHorarioSessao.setServidor(rapServidores);
			procedimentoTerapeuticoFacade.persistirMptHorarioSessao(mptHorarioSessao);
			
			RapServidoresId idAutorizadoPor = new RapServidoresId(this.autorizadoPor.getMatricula(), this.autorizadoPor.getVinculo());
			RapServidores autorizadoPor = this.registroColaboradorFacade.obterRapServidor(idAutorizadoPor);
			
			RapServidoresId idSolicitadoPor = new RapServidoresId(this.solicitadoPor.getMatricula(), this.solicitadoPor.getVinculo());
			RapServidores solicitadoPor = this.registroColaboradorFacade.obterRapServidor(idSolicitadoPor);
			
			MptSessaoExtra mptSessaoExtra = new MptSessaoExtra();
			mptSessaoExtra.setMptHorarioSessao(mptHorarioSessao);
			mptSessaoExtra.setRapServidores(autorizadoPor);
			mptSessaoExtra.setRapServidoresSolicitado(solicitadoPor);
			mptSessaoExtra.setMptJustificativa(this.mptJustificativa);
			mptSessaoExtra.setJustificativa(this.complementoJustificativa);
			//NÃO DEFINIDO EM DOCUMENTAÇÃO !!!!
			mptSessaoExtra.setCriadoEm(new Date());
			
			procedimentoTerapeuticoFacade.persistirMptSessaoExtra(mptSessaoExtra);
			
			MptExtratoSessao mptExtratoSessao = new MptExtratoSessao();
			mptExtratoSessao.setMptSessao(mptSessao);
			mptExtratoSessao.setIndSituacao(DominioSituacaoSessao.SEX);
			mptExtratoSessao.setCriadoEm(new Date());
			mptExtratoSessao.setServidor(rapServidores);
			
			procedimentoTerapeuticoFacade.persistirExtratoSessao(mptExtratoSessao);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AGENDAMENTO_EXTRA_AGENDAR_SESSAO");
			limpar(); 
			
		} catch (ApplicationBusinessException e) {
			
			if(this.horaInicio == null){
				this.horaFim = null;
				this.diaPrescricaoVOSelecionado = new DiaPrescricaoVO();
				this.diaPrescricaoVO = new DiaPrescricaoVO();
			}
			
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void limpar(){
		limparListas();
		this.autorizadoPor = null;
		this.solicitadoPor = null;
		this.complementoJustificativa = null;
		this.dataAtual = new Date();
		this.horaInicio = null;
		this.horaFim = null;
		this.complementoJustificativa = null;
		this.mptJustificativa = null;
		this.mptTipoSessaoSelecionado = null;
		this.listaMptTipoSessao = null;
		this.mptSalasSelecionado = null;
		this.listaMptSalas = null;
		this.rapServidores = null;
		this.pacCodigo = null;
		this.paciente = null;
		try {
			this.inicializar();
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage());
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparListas(){
		this.listaPrescricaoPacientePTVO = null;
		this.prescricaoPacientePTVO = new PrescricaoPacientePTVO();
		this.prescricaoPacientePTVOSelecionado = new PrescricaoPacientePTVO();
		this.listaDiaPrescricaoVO = null;
		this.diaPrescricaoVO = new DiaPrescricaoVO();
		this.diaPrescricaoVOSelecionado = new DiaPrescricaoVO();
	}
	
	public void obterDiaPrescricaoVO(){
		
		for (PrescricaoPacientePTVO pacientePTVO : listaPrescricaoPacientePTVO) {
			if(this.prescricaoPacientePTVO.getCloSeq().equals(pacientePTVO.getCloSeq())){
				this.prescricaoPacientePTVOSelecionado = pacientePTVO;
			}
		}
		
		listaDiaPrescricaoVO = this.procedimentoTerapeuticoFacade.obterDiaPrescricaoVO(this.prescricaoPacientePTVOSelecionado.getLote(), 
				this.prescricaoPacientePTVOSelecionado.getCloSeq());
	}
	
	public void relacionarDiaSelecionado(){
		
		try {
			for(DiaPrescricaoVO diaPrescricao : listaDiaPrescricaoVO){
				if(diaPrescricao.getSeq().equals(this.diaPrescricaoVO.getSeq())){
					this.diaPrescricaoVOSelecionado = diaPrescricao;
				}
			}
			this.horaFim = this.procedimentoTerapeuticoFacade.relacionarDiaSelecionado(this.horaInicio, diaPrescricaoVOSelecionado);
			} catch (ApplicationBusinessException e) {
				apresentarMsgNegocio("horaInicio", Severity.ERROR, e.getMessage());
				LOG.error(e.getMessage());
				this.diaPrescricaoVO = new DiaPrescricaoVO();
				this.diaPrescricaoVOSelecionado = new DiaPrescricaoVO();
			}
	}
	
	public List<RapServidoresProcedimentoTerapeuticoVO> obterServidorAutorizadoSolicitadoSB(String descricao){
		return this.returnSGWithCount(this.registroColaboradorFacade.obterListaServidoresAtivos(descricao), 
				this.obterServidorAutorizadoSolicitadoSBCount(descricao)); 
	}
	
	public Long obterServidorAutorizadoSolicitadoSBCount(String descricao){
		return this.registroColaboradorFacade.obterListaServidoresAtivosCount(descricao);
	}
	
	public List<MptJustificativa> obterListaJustificativaSB(String descricao){
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.obterListaJustificativaSB(descricao),
				this.obterListaJustificativaSBCount(descricao));
	}
	
	public Long obterListaJustificativaSBCount(String descricao){
		return this.procedimentoTerapeuticoFacade.obterListaJustificativaSBCount(descricao);
	}
	
	public String obterDiaSessao(Short dia){
		return "D"+dia.toString();		
	}
	
	public void validarAlteracaoHoraInicio(){
		if(this.horaInicio == null){
			this.horaFim = null;
			this.diaPrescricaoVOSelecionado = null;
		}
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
	
	public Date getDataAtual() {
		return dataAtual;
	}
	
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public RapServidoresProcedimentoTerapeuticoVO getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(
			RapServidoresProcedimentoTerapeuticoVO autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}

	public RapServidoresProcedimentoTerapeuticoVO getSolicitadoPor() {
		return solicitadoPor;
	}

	public void setSolicitadoPor(
			RapServidoresProcedimentoTerapeuticoVO solicitadoPor) {
		this.solicitadoPor = solicitadoPor;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		if(paciente == null){
			limparListas();
		}
	}

	public List<MptTipoSessao> getListaMptTipoSessao() {
		return listaMptTipoSessao;
	}

	public void setListaMptTipoSessao(List<MptTipoSessao> listaMptTipoSessao) {
		this.listaMptTipoSessao = listaMptTipoSessao;
	}

	public List<MptSalas> getListaMptSalas() {
		return listaMptSalas;
	}

	public void setListaMptSalas(List<MptSalas> listaMptSalas) {
		this.listaMptSalas = listaMptSalas;
	}

	public MptTipoSessao getMptTipoSessaoSelecionado() {
		return mptTipoSessaoSelecionado;
	}

	public void setMptTipoSessaoSelecionado(MptTipoSessao mptTipoSessaoSelecionado) {
		this.mptTipoSessaoSelecionado = mptTipoSessaoSelecionado;
	}

	public MptSalas getMptSalasSelecionado() {
		return mptSalasSelecionado;
	}

	public void setMptSalasSelecionado(MptSalas mptSalasSelecionado) {
		this.mptSalasSelecionado = mptSalasSelecionado;
	}

	public MptJustificativa getMptJustificativa() {
		return mptJustificativa;
	}

	public void setMptJustificativa(MptJustificativa mptJustificativa) {
		this.mptJustificativa = mptJustificativa;
	}

	public String getComplementoJustificativa() {
		return complementoJustificativa;
	}

	public void setComplementoJustificativa(String complementoJustificativa) {
		this.complementoJustificativa = complementoJustificativa;
	}

	public List<PrescricaoPacientePTVO> getListaPrescricaoPacientePTVO() {
		return listaPrescricaoPacientePTVO;
	}

	public void setListaPrescricaoPacientePTVO(
			List<PrescricaoPacientePTVO> listaPrescricaoPacientePTVO) {
		this.listaPrescricaoPacientePTVO = listaPrescricaoPacientePTVO;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public List<DiaPrescricaoVO> getListaDiaPrescricaoVO() {
		return listaDiaPrescricaoVO;
	}

	public void setListaDiaPrescricaoVO(List<DiaPrescricaoVO> listaDiaPrescricaoVO) {
		this.listaDiaPrescricaoVO = listaDiaPrescricaoVO;
	}

	public DiaPrescricaoVO getDiaPrescricaoVOSelecionado() {
		return diaPrescricaoVOSelecionado;
	}

	public void setDiaPrescricaoVOSelecionado(
			DiaPrescricaoVO diaPrescricaoVOSelecionado) {
		this.diaPrescricaoVOSelecionado = diaPrescricaoVOSelecionado;
	}

	public PrescricaoPacientePTVO getPrescricaoPacientePTVO() {
		return prescricaoPacientePTVO;
	}

	public void setPrescricaoPacientePTVO(
			PrescricaoPacientePTVO prescricaoPacientePTVO) {
		this.prescricaoPacientePTVO = prescricaoPacientePTVO;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public PrescricaoPacientePTVO getPrescricaoPacientePTVOSelecionado() {
		return prescricaoPacientePTVOSelecionado;
	}

	public void setPrescricaoPacientePTVOSelecionado(
			PrescricaoPacientePTVO prescricaoPacientePTVOSelecionado) {
		this.prescricaoPacientePTVOSelecionado = prescricaoPacientePTVOSelecionado;
	}

	public DiaPrescricaoVO getDiaPrescricaoVO() {
		return diaPrescricaoVO;
	}

	public void setDiaPrescricaoVO(DiaPrescricaoVO diaPrescricaoVO) {
		this.diaPrescricaoVO = diaPrescricaoVO;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}
