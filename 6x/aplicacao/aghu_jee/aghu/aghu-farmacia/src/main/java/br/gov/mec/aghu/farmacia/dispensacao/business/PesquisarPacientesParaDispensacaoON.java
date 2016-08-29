package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dao.VAfaPrcrDispMdtosDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@Stateless
public class PesquisarPacientesParaDispensacaoON extends BaseBusiness
		implements Serializable {


@EJB
private PesquisarPacientesParaDispensacaoRN pesquisarPacientesParaDispensacaoRN;

private static final Log LOG = LogFactory.getLog(PesquisarPacientesParaDispensacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private VAfaPrcrDispMdtosDAO vAfaPrcrDispMdtosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8530284855445593521L;

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(Object strPesquisa) {
		
		List<AghUnidadesFuncionais.Fields> fieldsOrder = Arrays.asList(
				AghUnidadesFuncionais.Fields.ANDAR,
				AghUnidadesFuncionais.Fields.ALA,
				AghUnidadesFuncionais.Fields.DESCRICAO);
		
		return getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
				strPesquisa, DominioSituacao.A, Boolean.TRUE,Boolean.TRUE, Boolean.TRUE, 
					fieldsOrder, ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
	}
	
	public Long listarFarmaciasAtivasByPesquisaCount(Object strPesquisa) {
		
		return getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				strPesquisa, DominioSituacao.A, Boolean.TRUE,Boolean.TRUE, Boolean.TRUE,
					ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
	}
	
	public List<VAfaPrcrDispMdtos> pesquisarPacientesParaDispensacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Date dataReferencia, AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes pacientePesquisa) throws ApplicationBusinessException {
		
		Date dataInicial = obterDataInicial(dataReferencia);
		Date dataFinal = obterDataFinal(dataReferencia);
		
//		List<VAfaPrcrDisp> pacientes = getVAfaPrcrDispDAO().pesquisarPacientesParaDispensacao(
//				firstResult, maxResult, orderProperty, asc, dataReferencia,
//				farmacia, unidadeFuncionalPrescricao, leito, quarto, pacientePesquisa,
//				dataInicial, dataFinal);
		
		List<VAfaPrcrDispMdtos> pacientes = vAfaPrcrDispMdtosDAO.pesquisarPacientesParaDispensacao(
				firstResult, maxResult, orderProperty, asc, dataReferencia,
				farmacia, unidadeFuncionalPrescricao, leito, quarto, pacientePesquisa,
				dataInicial, dataFinal);
		
		for(int i = 0; i<pacientes.size();i++){
			VAfaPrcrDispMdtos paciente = pacientes.get(i);
			paciente.setCorCelula(getPesquisarPacientesParaDispensacaoRN().afacOrdenaDisp(paciente));
		}
		
		CoreUtil.ordenarLista(pacientes, "prontuario", Boolean.FALSE);
		CoreUtil.ordenarLista(pacientes, "corCelula.codigo", Boolean.TRUE);
		
		Integer lastResult = (firstResult+maxResult)>pacientes.size()?pacientes.size():(firstResult+maxResult);
		
		pacientes = pacientes.subList(firstResult, lastResult);
		
		setAtributosTransient(pacientes);
		
		return pacientes;
	}
	
	/**
	 * Seta o horário de início da pesquisa. Para corrigir um defeito que ocorre quando
	 * muda para o horário de verão utilizaremos maior que o último minuto do dia anterior
	 */
	private Date obterDataInicial(Date data){
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(data);
		calendario.add(Calendar.DATE, -1);
		calendario.set(Calendar.HOUR_OF_DAY, 23);
		calendario.set(Calendar.MINUTE, 59);
		calendario.set(Calendar.SECOND, 59);
		calendario.set(Calendar.MILLISECOND, 999);
		return calendario.getTime();
	}
	
	/**
	 * Seta o horário de fim da pesquisa. Para corrigir um defeito que ocorre quando
	 * muda para o horário de verão utilizaremos maior que o último minuto do dia anterior
	 */
	private Date obterDataFinal(Date data){
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(data);
		calendario.set(Calendar.HOUR_OF_DAY, 23);
		calendario.set(Calendar.MINUTE, 59);
		calendario.set(Calendar.SECOND, 59);
		calendario.set(Calendar.MILLISECOND, 999);
		return calendario.getTime();
	}
	
	private void setAtributosTransient(List<VAfaPrcrDispMdtos> pacientes) throws ApplicationBusinessException {
		
		for(int i = 0; i<pacientes.size();i++){
			VAfaPrcrDispMdtos paciente = pacientes.get(i);
			
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(paciente.getId().getAtdSeq());
			paciente.setLocalizacaoPaciente(getPrescricaoMedicaFacade().buscarResumoLocalPaciente(atendimento));
			//paciente.setCorCelula(getPesquisarPacientesParaDispensacaoRN().afacOrdenaDisp(paciente.getId().getAtdSeq(), paciente.getId().getSeq()));
			paciente.setFlagExibePreparar(validaFlagExibirPreparar(paciente.getTrpSeq()));
			paciente.setFlagExibeTriarFunction1(validaFlagExibirTriar(paciente.getTrpSeq()));
			paciente.setFlagExibeTriarFunction2(!validaFlagExibirTriar(paciente.getTrpSeq()));
			paciente.setFlagExibeDispensar(validaFlagExibirDispensar(paciente.getTrpSeq()));
			
			pacientes.set(i, paciente);
		}
	}

	private Boolean validaFlagExibirDispensar(Integer trpSeq) {
		if(trpSeq==null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Se NÃO existir, chamar pagina correspondente a AFAF_TRIAG_DISP_SP ( passando ATD_SEQ e SEQ (prescriçao)).
	 * @param atendimento
	 * @return
	 */
	private Boolean validaFlagExibirTriar(Integer trpSeq) {
		if(trpSeq!=null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Se NÃO existir, ativar link “Preparar”. 
	 * @param atendimento
	 * @return
	 */
	private Boolean validaFlagExibirPreparar(Integer trpSeq) {
		if(trpSeq==null) {
			return true;
		}
		
		return false;
	}

	private PesquisarPacientesParaDispensacaoRN getPesquisarPacientesParaDispensacaoRN(){
		return pesquisarPacientesParaDispensacaoRN;
	}
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	public Long pesquisarPacientesParaDispensacaoCount(Date dataReferencia,
			AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente) {
		Date dataInicial = obterDataInicial(dataReferencia);
		Date dataFinal = obterDataFinal(dataReferencia);
		return vAfaPrcrDispMdtosDAO.pesquisarPacientesParaDispensacao(
				dataReferencia, farmacia, unidadeFuncionalPrescricao, leito,
				quarto, paciente, dataInicial, dataFinal);
	}
	
	public DominioSituacaoDispensacaoMdto[] pesquisarFiltroDispensacaoMdtos() {
			DominioSituacaoDispensacaoMdto[] ret = new DominioSituacaoDispensacaoMdto[4];
			ret[0] = DominioSituacaoDispensacaoMdto.C;
			ret[1] = DominioSituacaoDispensacaoMdto.D;
			ret[2] = DominioSituacaoDispensacaoMdto.E;
			ret[3] = DominioSituacaoDispensacaoMdto.S;
			return ret;
	}
}