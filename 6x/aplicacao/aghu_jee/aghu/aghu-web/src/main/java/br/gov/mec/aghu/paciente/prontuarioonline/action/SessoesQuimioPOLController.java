package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MptCidTratTerapeutico;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.protocolo.business.IProtocoloFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class SessoesQuimioPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 565478159836205411L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IProtocoloFacade ProtocoloFacade;
	
	private Integer seqTratamentoTerQuimio;
	private Date dtInicio;
	private Date dtFim;
	private String responsavel;
	private String codigoCid;
	private String descricaoCid;
	
	private Integer pacCodigo;
	private Integer atdSeq;

	@Inject @Paginator
	private DynamicDataModel<MpaProtocoloAssistencial> dataModel;
	

	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;		
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		iniciar();
	}

	
	public void iniciar() {
		if (itemPOL!=null){
			seqTratamentoTerQuimio=(Integer) itemPOL.getParametros().get("seqTratamentoTerapeutico"); 
		}
		
		dataModel.reiniciarPaginator();
		carregarDadosQuimio();
	}
	
	@Override
	public Long recuperarCount() {
		return getProtocoloFacade().consultarSessoesQuimioterapiaCount(seqTratamentoTerQuimio);
	}

	@Override
	public List<MpaProtocoloAssistencial> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getProtocoloFacade().consultarSessoesQuimioterapia(seqTratamentoTerQuimio, firstResult, maxResult);
	}

	protected void carregarDadosQuimio(){
		MptTratamentoTerapeutico tratamentoQuimio = getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(seqTratamentoTerQuimio);
		MptCidTratTerapeutico cidTratQuimio = getProntuarioOnlineFacade().obterCidTratamentoQuimio(seqTratamentoTerQuimio);
		dtInicio = tratamentoQuimio.getDthrInicio();
		dtFim = tratamentoQuimio.getDthrFim();
		
		RapPessoasFisicas rpf = registroColaboradorFacade.obterRapPessoasFisicas(tratamentoQuimio.getServidorResponsavel().getPessoaFisica().getCodigo());
		responsavel = rpf.getNome();
		
		codigoCid = cidTratQuimio.getAghCid().getCodigo();
		descricaoCid = cidTratQuimio.getAghCid().getDescricao();
		pacCodigo = tratamentoQuimio.getPaciente().getCodigo();
		atdSeq = getProntuarioOnlineFacade().obterAtdSeq(tratamentoQuimio.getSeq());
	}
	

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}


	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public Integer getSeqTratamentoTerQuimio() {
		return seqTratamentoTerQuimio;
	}

	public void setSeqTratamentoTerQuimio(Integer seqTratamentoTerQuimio) {
		this.seqTratamentoTerQuimio = seqTratamentoTerQuimio;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getCodigoCid() {
		return codigoCid;
	}

	public void setCodigoCid(String codigoCid) {
		this.codigoCid = codigoCid;
	}

	public String getDescricaoCid() {
		return descricaoCid;
	}

	public void setDescricaoCid(String descricaoCid) {
		this.descricaoCid = descricaoCid;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public void setProtocoloFacade(IProtocoloFacade protocoloFacade) {
		ProtocoloFacade = protocoloFacade;
	}

	public IProtocoloFacade getProtocoloFacade() {
		return ProtocoloFacade;
	}

	public DynamicDataModel<MpaProtocoloAssistencial> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpaProtocoloAssistencial> dataModel) {
	 this.dataModel = dataModel;
	}
}
