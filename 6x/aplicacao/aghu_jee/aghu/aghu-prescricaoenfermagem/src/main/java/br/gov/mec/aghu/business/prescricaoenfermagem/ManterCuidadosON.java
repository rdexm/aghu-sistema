package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EpeCuidadoUnf;
import br.gov.mec.aghu.model.EpeCuidadoUnfId;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoUnfDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeItemCuidadoSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCuidadosON extends BaseBusiness {

	@EJB
	private ManterCuidadosRN manterCuidadosRN;
	
	private static final Log LOG = LogFactory.getLog(ManterCuidadosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EpeCuidadoUnfDAO epeCuidadoUnfDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private EpeCuidadosDAO epeCuidadosDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@Inject
	private EpeItemCuidadoSumarioDAO epeItemCuidadoSumarioDAO;
	
	@Inject
	private EpeCuidadoDiagnosticoDAO epeCuidadoDiagnosticoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	private static final long serialVersionUID = 7631528807612164559L;
	
	/**
	 * Mensagens de negocio
	 *
	 */
	public enum ManterCuidadosONExceptionCode implements BusinessExceptionCode {
		MSG_CUIDADO_DIAGNOSTICO_RELACIONADO, // Cuidado não pode ser excluido pois existem "Cuidados de Diagnósticos" relacionados
		MSG_UNIDADE_FUNCIONAL_RELACIONADO, // Cuidado não pode ser excluido pois existem "Unidades Funcionais" relacionadas
		MSG_ITEM_CUIDADO_SUMARIO_RELACIONADO, // Cuidado não pode ser excluido pois existem "Item Cuidado Sumarios" relacionados
		MSG_PRESCRICAO_CUIDADO_RELACIONADO, // Cuidado não pode ser excluido pois existem "Prescrições de Cuidados" relacionados
		MSG_CUIDADO_UNIDADE_RELACIONADAS, //Cuidado de Rotina não pode ser desmarcado enquanto houverem Unidades do Cuidado de Rotina relacionadas.
		EPE_00225, // Não é possível excluir o registro por estar fora do período permitido para exclusão
		EPE_00228, // Frequência não pode ser informada sem o tipo
		EPE_00229, // Não existe tipo de frequência com este valor
		EPE_00230, // Frequência não pode ser informada.
		EPE_00231, // Frequência obrigatoriamente deve ser informada.
		MSG_EPE_CUIDADO_UNF_JA_CADASTRADA,
		MENSAGEM_CUIDADO_JA_EXISTENTE;
	}

	public void inserir(EpeCuidados cuidado) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		Short frequencia = 0;
		Short tipoFrequencia = 0;
		
		if(cuidado.getFrequencia() != null) {
			frequencia = cuidado.getFrequencia();
			tipoFrequencia = cuidado.getTipoFrequenciaAprazamento().getSeq();
		}
		if(frequencia != null && frequencia.intValue() > 0) {
			manterCuidadosRN.verificarFrequencia(tipoFrequencia, frequencia);
		}
		cuidado.setCriadoEm(Calendar.getInstance().getTime());
		cuidado.setServidor(servidorLogado);		

		epeCuidadosDAO.persistir(cuidado);	
		
		bancoDeSangueFacade.inserirFatProcedHospInternos(null, null, null, null, null, cuidado.getDescricao(), cuidado.getIndSituacao(), 
				  											  null, null, cuidado.getSeq(), null);
		
	}
	
	public void atualizar(EpeCuidados original, EpeCuidados modificado) throws ApplicationBusinessException {
		manterCuidadosRN.executarAntesDeAtualizar(original, modificado);
		epeCuidadosDAO.merge(modificado);
	}
	
	public void validarCuidadoRotina(Boolean indRotina, List<EpeCuidadoUnf> listaCuidadoUnidades) throws ApplicationBusinessException {
		if (!indRotina && listaCuidadoUnidades != null && !listaCuidadoUnidades.isEmpty()) {
			throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.MSG_CUIDADO_UNIDADE_RELACIONADAS);
		}
	}
	
	public String gravarEpeCuidadoUnfs(EpeCuidadoUnf epeCuidadoUnf, EpeCuidados epeCuidado, AghUnidadesFuncionais aghUnidadeFuncional) throws ApplicationBusinessException {
		epeCuidadoUnf.setId(new EpeCuidadoUnfId(epeCuidado.getSeq(), aghUnidadeFuncional.getSeq())); 
		
		if(getEpeCuidadoUnfDAO().obterPorChavePrimaria(epeCuidadoUnf.getId()) != null){
				throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.MSG_EPE_CUIDADO_UNF_JA_CADASTRADA);
		}
			
		epeCuidadoUnf.setId(new EpeCuidadoUnfId(epeCuidadoUnf.getId().getCuiSeq(), epeCuidadoUnf.getId().getUnfSeq())); 
		getEpeCuidadoUnfDAO().persistir(epeCuidadoUnf);
		return "MSG_EPE_CUIDADO_UNF_INSERCAO_COM_SUCESSO";			
	}	
	
	public String alterarSituacao(EpeCuidadoUnf epeCuidadoUnf) {
		if (epeCuidadoUnf.getSituacao().equals(DominioSituacao.A)) {
			epeCuidadoUnf.setSituacao(DominioSituacao.I);
		} else {
			epeCuidadoUnf.setSituacao(DominioSituacao.A);
		}
		getEpeCuidadoUnfDAO().atualizar(epeCuidadoUnf);
		return "MSG_EPE_CUIDADO_UNF_ALTERACAO_COM_SUCESSO";
	}	
	
	public void removerEpeCuidadoUnf(EpeCuidadoUnfId id) throws ApplicationBusinessException {
		epeCuidadoUnfDAO.removerPorId(id);
	}
		
	
	public void restaurarEpeCuidadoUnf(List<EpeCuidadoUnf> listaCuidadoUnidades) {
		final EpeCuidadoUnfDAO epeCuidadoUnfDAO = getEpeCuidadoUnfDAO();
		
		for(EpeCuidadoUnf item : listaCuidadoUnidades){
			if(epeCuidadoUnfDAO.contains(item)){
				epeCuidadoUnfDAO.refresh(item);
			}
		}		
	}
	
	public String gravar(EpeCuidados epeCuidadosEdicao, Short codigo) throws ApplicationBusinessException {
		
		EpeCuidados cuidadoOriginal = null;
		
		List<EpeCuidados> epCuidList = getEpeCuidadosDAO().pesquisarEpeCuidadosPorCodigoDescricao(null, epeCuidadosEdicao.getDescricao(), Integer.valueOf(0), 
				Integer.valueOf(10), EpeCuidados.Fields.DESCRICAO.toString(), false, false);
		if (epCuidList!=null && epCuidList.size()>1){//1 porque tem o registro que está sendo alterado.
			for (EpeCuidados epeCuid : epCuidList){
				if (!epeCuid.getSeq().equals(epeCuidadosEdicao.getSeq()) && epeCuid.getDescricao().equals(epeCuidadosEdicao.getDescricao())){
					throw new ApplicationBusinessException(ManterCuidadosONExceptionCode.MENSAGEM_CUIDADO_JA_EXISTENTE);
				}
			}	
		}
		if (codigo != null) {
			cuidadoOriginal = getEpeCuidadosDAO().obterOriginal(codigo);
			//epeCuidadosEdicao.setSeq(codigo);
			atualizar(cuidadoOriginal, epeCuidadosEdicao);
			return "MSG_SUCESSO_ALTERACAO_CUIDADO";
		} else {
			epeCuidadosEdicao.setIndCci(Boolean.FALSE);		
			inserir(epeCuidadosEdicao);
			return "MSG_SUCESSO_INSERCAO_CUIDADO";
		}
		
	} 

	public String excluirEpeCuidados(Short seq) throws ApplicationBusinessException, BaseListException, ApplicationBusinessException {
		manterCuidadosRN.verificarAntesDeExcluir(seq); // RN1
		EpeCuidados epeCuidadosDelecao = epeCuidadosDAO.obterPorChavePrimaria(seq);
		manterCuidadosRN.verificarPeriodo(epeCuidadosDelecao.getCriadoEm()); // RN2
		this.getFaturamentoFacade().deleteFatProcedHospInternos(null, null, null, null, null, null, null, seq, null, Boolean.FALSE);// RN7
		epeCuidadosDAO.removerPorId(seq);
		return "MSG_SUCESSO_EXCLUSAO_CUIDADO";
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
		
	protected EpeCuidadosDAO getEpeCuidadosDAO() {
		return epeCuidadosDAO;
	}
	
	protected EpeCuidadoDiagnosticoDAO getEpeCuidadoDiagnosticoDAO() {
		return epeCuidadoDiagnosticoDAO;
	}
	
	protected EpeCuidadoUnfDAO getEpeCuidadoUnfDAO() {
		return epeCuidadoUnfDAO;
	}
	
	protected EpeItemCuidadoSumarioDAO getEpeItemCuidadoSumarioDAO() {
		return epeItemCuidadoSumarioDAO;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	protected ICadastrosBasicosPrescricaoMedicaFacade getCadastrosBasicosPrescricaoMedicaFacade(){
		return cadastrosBasicosPrescricaoMedicaFacade;
	}
	
	protected ManterCuidadosRN getManterCuidadosRN() {
		return manterCuidadosRN;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
