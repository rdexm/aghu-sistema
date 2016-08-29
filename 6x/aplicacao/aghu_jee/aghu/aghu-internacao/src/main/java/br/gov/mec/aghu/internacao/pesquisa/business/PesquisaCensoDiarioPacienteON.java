package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoCensoDiarioPacientes;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AhdHospitaisDiaDAO;
import br.gov.mec.aghu.internacao.dao.AinDocsInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentosAtendUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinObservacoesCensoDAO;
import br.gov.mec.aghu.internacao.vo.VAinCensoVO;
import br.gov.mec.aghu.internacao.vo.VerificaPermissaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinObservacoesCenso;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamTipoEstadoPaciente;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.NcssTypeCount", "PMD.CyclomaticComplexity",
		"PMD.ExcessiveClassLength" })
@Stateless
public class PesquisaCensoDiarioPacienteON extends BaseBusiness {

	@EJB
	private PesquisaInternacaoRN pesquisaInternacaoRN;

	@Inject
	private IControleInfeccaoFacade controleInfeccaoFacade;

	private static final Log LOG = LogFactory.getLog(PesquisaCensoDiarioPacienteON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@Inject
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@Inject
	private AinObservacoesCensoDAO ainObservacoesCensoDAO;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private AhdHospitaisDiaDAO ahdHospitaisDiaDAO;

	@Inject
	private AinLeitosDAO ainLeitosDAO;

	@Inject
	private AinMovimentosAtendUrgenciaDAO ainMovimentosAtendUrgenciaDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionais;

	@Inject
	private AinDocsInternacaoDAO ainDocsInternacaoDAO;

	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6386076720960367716L;

	// private static final String ENTITY_MANAGER = "entityManager";

	/**
	 * Enumeracao com os codigos de mensagens de exceções negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum PesquisaCensoDiarioPacienteONExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_OBS
	}

 	/**
	 * Obtém o número de dias do Censo de acordo com parâmetro no banco
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private DominioSimNao obterCensoLeitoUnico() throws ApplicationBusinessException {
		if (parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.P_CENSO_LEITO_UNICO)) {
			AghParametros ap = parametroFacade.getAghParametro(AghuParametrosEnum.P_CENSO_LEITO_UNICO);
			if(ap != null  && ap.getVlrTexto() != null){
				return DominioSimNao.valueOf(ap.getVlrTexto().toUpperCase());
			}
		}
		return DominioSimNao.valueOf("N");
	}

	/**
	 * 
	 * @return List<VAinCensoVO> - lista de VAinCensoVO conforme os criterios de
	 *         pesquisa.
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('censoDiario','pesquisar')}")
	public Object[] pesquisarCensoDiarioPacientes(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status)
			throws ApplicationBusinessException {

		Boolean isCensoLeitoUnico = obterCensoLeitoUnico().isSim();
 		List<VAinCensoVO> lista = obterCensoDiarioPacientes(unfSeq, unfSeqMae, data, status);
 		
 		Collections.sort(lista, new Comparator<VAinCensoVO>() {
			@Override
			public int compare(VAinCensoVO c1, VAinCensoVO c2) {
				return c1.getQrtoLto().compareTo(c2.getQrtoLto());
			}
		});
 		
		if (isCensoLeitoUnico) {
			lista = SetUniqueList.decorate(lista);
			verificarPacienteComLeitoVazio(lista);
		}
		
		
		Long count = Long.valueOf(lista.size());
		
		List<VAinCensoVO> sublista = null;
		if (lista.size() < (firstResult + maxResults)) {
			sublista = lista.subList(firstResult, lista.size());
		} else {
			sublista = lista.subList(firstResult, firstResult + maxResults);
		}
		return new Object[] { count, sublista };

	}

	private void verificarPacienteComLeitoVazio(List<VAinCensoVO> lista) {
		List<VAinCensoVO> aux = new ArrayList<VAinCensoVO>();
		aux.addAll(lista);
		
		List<VAinCensoVO> listaRemover = new ArrayList<VAinCensoVO>(); 
		for (int i = 0; i < lista.size(); i++) {
			for (VAinCensoVO vcv : aux) {
				if(vcv.getProntuario() != null && lista.get(i).getProntuario() != null &&
						vcv.getProntuario().equals(lista.get(i).getProntuario())){
					if(!vcv.getQrtoLto().equals(lista.get(i).getQrtoLto())){
						if(vcv.getDthrLancamento().after(lista.get(i).getDthrLancamento())){
							listaRemover.add(lista.get(i));
						}
					}
				}
			}
		}
		
		if(listaRemover.size() >= 1){
			for (VAinCensoVO ainCensoVO : listaRemover) {
				lista.remove(ainCensoVO);
			}
		}
	}

	private List<VAinCensoVO> obterCensoDiarioPacientes(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();

		if (!status.equals(DominioSituacaoUnidadeFuncional.PACIENTES)
				|| (data != null && (DateUtils.truncate(data, Calendar.DAY_OF_MONTH)
						.before(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))))) {
			if (status.equals(DominioSituacaoUnidadeFuncional.PACIENTES)) {
				lista.addAll(obterCriteriaCensoUnion1(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion8(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion9(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion15(unfSeq, unfSeqMae, data, status));
			} else if (status.equals(DominioSituacaoUnidadeFuncional.INTERNACOES)) {
				lista.addAll(obterCriteriaCensoUnion2(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion10(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion14(unfSeq, unfSeqMae, data, status));
			} else if (status.equals(DominioSituacaoUnidadeFuncional.ALTAS)) {
				lista.addAll(obterCriteriaCensoUnion3(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion11(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion16(unfSeq, unfSeqMae, data, status));
			} else if (status.equals(DominioSituacaoUnidadeFuncional.RECEBIDOS)) {
				lista.addAll(obterCriteriaCensoUnion4(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion6(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion12(unfSeq, unfSeqMae, data, status));
			} else if (status.equals(DominioSituacaoUnidadeFuncional.ENVIADOS)) {
				lista.addAll(obterCriteriaCensoUnion5(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion7(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion13(unfSeq, unfSeqMae, data, status));
			}
		} else {
			if (status.equals(DominioSituacaoUnidadeFuncional.PACIENTES)) {
				lista.addAll(obterCriteriaCensoUnion17(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion18(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion19(unfSeq, unfSeqMae, data, status));
				lista.addAll(obterCriteriaCensoUnion20(unfSeq, unfSeqMae, data, status));
			}
		}
		
		if (obterCensoLeitoUnico().isSim()) {
			lista.addAll(obterCriteriaCensoUnionAll(unfSeq, unfSeqMae, data, status));
		}
		
		return lista;
	}

	public List<VAinCensoVO> pesquisarCensoDiarioPacientesSemPaginator(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		Boolean isCensoLeitoUnico = obterCensoLeitoUnico().isSim();
		List<VAinCensoVO> lista = obterCensoDiarioPacientes(unfSeq, unfSeqMae, data, status);
		
		Collections.sort(lista, new Comparator<VAinCensoVO>() {
			@Override
			public int compare(VAinCensoVO c1, VAinCensoVO c2) {
				return c1.getQrtoLto().compareTo(c2.getQrtoLto());
			}
		});

		if (isCensoLeitoUnico) {
			lista = SetUniqueList.decorate(lista);
		}
		
		return lista;
	}

	public AghUnidadesFuncionais obterUnidadeFuncional(Short unfSeq, Short unfSeqPai) {

		AghUnidadesFuncionais unf = aghUnidadesFuncionais.obterPorUnfSeq(unfSeq != null ? unfSeq : unfSeqPai);
		return unf;
	}

	public Integer pesquisarCensoDiarioPacientesCount(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {
		Integer count = 0;
		List<VAinCensoVO> lista = obterCensoDiarioPacientes(unfSeq, unfSeqMae, data, status);

		count = lista.size();

		return count;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 1ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion1(Short unfSeq, Short unfSeqMae, Date dataPesquisa,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion1(unfSeq, unfSeqMae, dataPesquisa, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();
		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[2]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[4]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setDthrLancamento((Date) obj[8]);
			}

			if (obj[9] != null) {
				vo.setInternacaoSeq((Integer) obj[9]);
			}

			if (obj[12] != null && obj[13] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[13], (Integer) obj[12]));
			}

			String leito = null;
			if (((AinLeitos) obj[11]) != null) {
				leito = ((AinLeitos) obj[11]).getLeitoID();
			} else {
				if (((AinQuartos) obj[10]) != null) {
					leito = ((AinQuartos) obj[10]).getDescricao();
				} else {
					leito = " ";
				}
			}
			vo.setQrtoLto(leito);

			if (obj[3] != null && obj[5] != null) {
				vo.setLocal(internacaoFacade.obtemLocalOrigem(((Integer) obj[9]), (Date) obj[8]));
			}

			if (obj[8] != null && obj[9] != null) {
				vo.setDthrLancamentoFinal(internacaoFacade.obtemDthrFinal(((Integer) obj[9]), (Date) obj[8]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[14] != null) {
				vo.setCspCnvCodigo(((Short) obj[14]));
			}

			if (obj[15] != null) {
				vo.setDtNascPaciente(((Date) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescConvenio(((String) obj[16]));
			}

			if (obj[17] != null) {
				vo.setTamDescricao(((String) obj[17]));
			}

			if (obj[18] != null) {
				vo.setDescricaoEsp(((String) obj[18]));
			}

			// Acreditem, os ifs abaixos foram simplificados !
			if (seDataPesquisaAnteriorHojeEStatusPaciente(dataPesquisa, status)) {
				dataPesquisaUltimaHora(dataPesquisa);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((dataPesquisa.after(vo.getDthrLancamento()) || dataPesquisa.equals(vo.getDthrLancamento()))
						&& (dataPesquisa.before(dtLancFinal) || dataPesquisa.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, dataPesquisa, status);
				lista.add(vo);
			}
		}
		return lista;
	}

	private void dataPesquisaUltimaHora(Date dataPesquisa) {
		Calendar dataLanc = Calendar.getInstance();
		dataLanc.setTime(dataPesquisa);
		dataLanc.set(Calendar.HOUR_OF_DAY, 23);
		dataLanc.set(Calendar.MINUTE, 59);
		dataLanc.set(Calendar.SECOND, 0);
		dataLanc.set(Calendar.MILLISECOND, 0);
		dataPesquisa.setTime(dataLanc.getTime().getTime());
	}

	private boolean seDataPesquisaAnteriorHojeEStatusPaciente(
			Date dataPesquisa, DominioSituacaoUnidadeFuncional status) {
		// TODO Auto-generated method stub
		return dataPesquisa != null
				&& dataPesquisa.before(DateUtil.truncaData(new Date()))
				|| !DominioSituacaoUnidadeFuncional.PACIENTES.equals(status);
	}
	
	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 2ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion2(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion2(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[2]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[4]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[5] != null) {
				vo.setDthrLancamento((Date) obj[5]);
			}

			if (obj[9] != null) {
				vo.setInternacaoSeq((Integer) obj[9]);
			}

			if (obj[12] != null && obj[13] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[13], (Integer) obj[12]));
			}

			String leito = null;
			if (((AinLeitos) obj[11]) != null) {
				leito = ((AinLeitos) obj[11]).getLeitoID();
			} else {
				if (((AinQuartos) obj[10]) != null) {
					leito = ((AinQuartos) obj[10]).getDescricao();
				} else {
					leito = " ";
				}
			}
			vo.setQrtoLto(leito);

			vo.setLocal(null);

			if (obj[5] != null) {
				Date dataTemp = (Date) obj[5];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.INTERNACOES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[14] != null) {
				vo.setCspCnvCodigo(((Short) obj[14]));
			}

			if (obj[15] != null) {
				vo.setDtNascPaciente(((Date) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescConvenio(((String) obj[16]));
			}

			if (obj[17] != null) {
				vo.setTamDescricao(((String) obj[17]));
			}

			if (obj[18] != null) {
				vo.setDescricaoEsp(((String) obj[18]));
			}

			if (data != null 
					&& (data.before(DateUtil.truncaData(new Date())) 
							|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);


				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 3ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion3(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion3(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;

			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[2]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[4]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setDthrLancamento((Date) obj[8]);
			}

			if (obj[9] != null) {
				vo.setInternacaoSeq((Integer) obj[9]);
			}

			if (obj[12] != null && obj[13] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[13], (Integer) obj[12]));
			}

			String leito = null;
			if (((AinLeitos) obj[11]) != null) {
				leito = ((AinLeitos) obj[11]).getLeitoID();
			} else {
				if (((AinQuartos) obj[10]) != null) {
					leito = ((AinQuartos) obj[10]).getDescricao();
				} else {
					leito = " ";
				}
			}
			vo.setQrtoLto(leito);

			vo.setLocal(null);

			if (obj[8] != null) {
				Date dataTemp = (Date) obj[8];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.ALTAS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[14] != null) {
				vo.setCspCnvCodigo(((Short) obj[14]));
			}

			if (obj[15] != null) {
				vo.setDtNascPaciente(((Date) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescConvenio(((String) obj[16]));
			}

			if (obj[17] != null) {
				vo.setTamDescricao(((String) obj[17]));
			}

			if (obj[18] != null) {
				vo.setDescricaoEsp(((String) obj[18]));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}

		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 4ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion4(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion4(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;

			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[2]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[4]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setDthrLancamento((Date) obj[8]);
			}

			if (obj[9] != null) {
				vo.setInternacaoSeq((Integer) obj[9]);
			}

			if (obj[12] != null && obj[13] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[13], (Integer) obj[12]));
			}

			String leito = null;
			if (((AinLeitos) obj[11]) != null) {
				leito = ((AinLeitos) obj[11]).getLeitoID();
			} else {
				if (((AinQuartos) obj[10]) != null) {
					leito = ((AinQuartos) obj[10]).getDescricao();
				} else {
					leito = " ";
				}
			}
			vo.setQrtoLto(leito);

			if (obj[9] != null && obj[8] != null) {
				vo.setLocal(internacaoFacade.obtemLocalOrigem(((Integer) obj[9]), (Date) obj[8]));
			}

			if (obj[8] != null) {
				Date dataTemp = (Date) obj[8];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.RECEBIDOS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[14] != null) {
				vo.setCspCnvCodigo(((Short) obj[14]));
			}

			if (obj[15] != null) {
				vo.setDtNascPaciente(((Date) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescConvenio(((String) obj[16]));
			}

			if (obj[17] != null) {
				vo.setTamDescricao(((String) obj[17]));
			}

			if (obj[18] != null) {
				vo.setDescricaoEsp(((String) obj[18]));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 5ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion5(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion5(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[15] != null && obj[10] != null) {
				vo.setUnfSeq(internacaoFacade.obtemLocalOrigemSeq(((Integer) obj[10]), (Date) obj[15]));
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null) {
				vo.setDthrLancamento((Date) obj[9]);
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[11] != null && obj[12] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[12], (Integer) obj[11]));
			}

			if (obj[2] != null) {
				vo.setQrtoLto((String) obj[2]);
			}

			if (obj[14] != null) {
				vo.setLocal((String) obj[14]);
			}

			if (obj[16] != null) {
				Date dataTemp = (Date) obj[16];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			if (obj[17] != null) {
				vo.setDtNascPaciente((Date) obj[17]);
			}

			if (obj[18] != null) {
				vo.setDescConvenio((String) obj[18]);
			}

			if (obj[19] != null) {
				vo.setTamDescricao(((String) obj[19]));
			}

			if (obj[20] != null) {
				vo.setDescricaoEsp(((String) obj[20]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.ENVIADOS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[13] != null) {
				vo.setCspCnvCodigo(((Short) obj[13]));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * Obtém o número de dias do Censo de acordo com parâmetro no banco
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Integer obterNumeroDiasCenso() throws ApplicationBusinessException {
		AghuParametrosEnum parametroEnumNumeroDias = AghuParametrosEnum.P_CENSO_ANTECEDENCIA_DIAS;
		AghParametros parametroNumeroDias = parametroFacade.buscarAghParametro(parametroEnumNumeroDias);
		BigDecimal numeroDiasCenso = parametroNumeroDias.getVlrNumerico();
		return numeroDiasCenso.intValue();
	}

	/**
	 * Obtém o código do leito bloqueado para limpeza de acordo com parâmetro no
	 * banco
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Integer obterCodigoLeitoBloqueioLimpeza() throws ApplicationBusinessException {
		AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA;
		AghParametros parametro = parametroFacade.buscarAghParametro(parametroEnum);
		BigDecimal codigoBloqueioLimpeza = parametro.getVlrNumerico();
		return codigoBloqueioLimpeza.intValue();
	}

	/**
	 * Obtém os códigos de leito ocupado e alta de acordo com parâmetro no banco
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Short obterCodigoLeitoOcupado() throws ApplicationBusinessException {
		AghuParametrosEnum parametroEnumOcupado = AghuParametrosEnum.P_COD_MVTO_LTO_OCUPADO;
		AghParametros parametroOcupado = parametroFacade.buscarAghParametro(parametroEnumOcupado);
		BigDecimal codigoOcupado = parametroOcupado.getVlrNumerico();
		return codigoOcupado.shortValue();
	}

	/**
	 * Obtém os códigos de leito ocupado e alta de acordo com parâmetro no banco
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Short obterCodigoLeitoAlta() throws ApplicationBusinessException {
		AghuParametrosEnum parametroEnumAlta = AghuParametrosEnum.P_COD_MVTO_LTO_ALTA;
		AghParametros parametroAlta = parametroFacade.buscarAghParametro(parametroEnumAlta);
		BigDecimal codigoAlta = parametroAlta.getVlrNumerico();
		return codigoAlta.shortValue();
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 6ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion6(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion6(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null) {
				vo.setDthrLancamento((Date) obj[9]);

				Date dthrLancamentoFinal = (Date) obj[9];
				dthrLancamentoFinal.setHours(23);
				dthrLancamentoFinal.setMinutes(59);
				vo.setDthrLancamentoFinal(dthrLancamentoFinal);
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[11] != null && obj[12] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[12], (Integer) obj[11]));
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[14] != null && obj[15] != null) {
				vo.setLocal(internacaoFacade.obtemLocalOrigem(((Integer) obj[14]), (Date) obj[15]));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (obj[16] != null) {
				vo.setDtNascPaciente((Date) obj[16]);
			}

			if (obj[17] != null) {
				vo.setDescConvenio((String) obj[17]);
			}

			if (obj[18] != null) {
				vo.setTamDescricao(((String) obj[18]));
			}

			if (obj[19] != null) {
				vo.setDescricaoEsp(((String) obj[19]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.RECEBIDOS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[13] != null) {
				vo.setCspCnvCodigo(((Short) obj[13]));
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 7ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion7(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnion7(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[11] != null && obj[12] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[12], (Integer) obj[11]));
			}

			if (obj[10] != null && obj[9] != null) {
				vo.setLocal(internacaoFacade.obtemLocalDestino(((Integer) obj[10]), (Date) obj[9]));
			}

			if (obj[10] != null && obj[9] != null) {
				vo.setDthrLancamento(internacaoFacade.obtemDthrFinal(((Integer) obj[10]), (Date) obj[9]));
			}

			if (obj[10] != null && obj[9] != null) {
				vo.setDthrLancamentoFinal(internacaoFacade.obtemDthrFinalT(((Integer) obj[10]), (Date) obj[9]));
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[14] != null) {
				vo.setDtNascPaciente((Date) obj[14]);
			}

			if (obj[15] != null) {
				vo.setDescConvenio((String) obj[15]);
			}

			if (obj[16] != null) {
				vo.setTamDescricao(((String) obj[16]));
			}

			if (obj[17] != null) {
				vo.setDescricaoEsp(((String) obj[17]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.ENVIADOS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[13] != null) {
				vo.setCspCnvCodigo(((Short) obj[13]));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}
				if (vo.getDthrLancamento() != null) {
					if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
							&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
						adicionar = false;
					}
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 8ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion8(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainExtratoLeitosDAO.obterCensoUnion8(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			vo.setOrigemNomeSituacao(true);

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setQrtoLto((String) obj[2]);
			}

			if (obj[3] != null) {
				vo.setNomeSituacao((String) obj[3]);
			}

			if (obj[4] != null) {
				vo.setDthrLancamento((Date) obj[4]);
			}

			if (obj[2] != null && obj[4] != null) {
				vo.setDthrLancamentoFinal(internacaoFacade.obtemDthrFinalLeito(((String) obj[2]), (Date) obj[4]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			if (obj[5] != null) {
				vo.setGrupoMvtoLeito(((DominioMovimentoLeito) obj[5]));
			}

			if (obj[6] != null) {
				vo.setSeqExtrato((Integer) obj[6]);
			}

			vo.setTipo(DominioTipoCensoDiarioPacientes.L);

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}

			}

			if (lista.indexOf(vo) != -1) {
				VAinCensoVO censo = lista.get(lista.indexOf(vo));
				if (censo.getSeqExtrato() > vo.getSeqExtrato()) {
					adicionar = false;
				} else {
					lista.remove(censo);
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 9ª UNION da view
	 * V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion9(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentosAtendUrgenciaDAO.obterCensoUnion9(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[10] != null && obj[9] != null) {
				vo.setLocal(internacaoFacade.obtemLocalOrigemUrgencia(((Integer) obj[10]), (Date) obj[9]));
			}

			if (obj[9] != null) {
				vo.setDthrLancamento((Date) obj[9]);
			}

			if (obj[10] != null && obj[9] != null) {
				vo.setDthrLancamentoFinal(internacaoFacade.obtemDthrFinalNova((Integer) obj[10], (Date) obj[9],
						(Integer) obj[10], (Date) obj[11]));
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[13] != null) {
				vo.setTamDescricao(((String) obj[13]));
			}

			if (obj[14] != null) {
				vo.setDescricaoEsp(((String) obj[14]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.A);

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	public Boolean mostrarEstadoSaude(Short seqUnidadeFuncional) {
		return this.internacaoFacade.verificarCaracteristicaUnidadeFuncional(seqUnidadeFuncional,
				ConstanteAghCaractUnidFuncionais.INFORMA_ESTADO_SAUDE);
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 10ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion10(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentosAtendUrgenciaDAO.obterCensoUnion10(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[6] != null) {
				vo.setDthrLancamento((Date) obj[6]);
			}

			if (obj[6] != null) {
				Date dataTemp = (Date) obj[6];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[13] != null) {
				vo.setTamDescricao(((String) obj[13]));
			}

			if (obj[14] != null) {
				vo.setDescricaoEsp(((String) obj[14]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.INTERNACOES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.A);

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 11ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion11(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentosAtendUrgenciaDAO.obterCensoUnion11(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null) {
				vo.setDthrLancamento((Date) obj[9]);
			}

			if (obj[9] != null) {
				Date dataTemp = (Date) obj[9];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[13] != null) {
				vo.setTamDescricao(((String) obj[13]));
			}

			if (obj[14] != null) {
				vo.setDescricaoEsp(((String) obj[14]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.ALTAS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.A);

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 12ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion12(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentosAtendUrgenciaDAO.obterCensoUnion12(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null) {
				vo.setLocal(internacaoFacade.obtemLocalOrigemUrgencia((Integer) obj[10], (Date) obj[9]));
			}

			if (obj[9] != null) {
				vo.setDthrLancamento((Date) obj[9]);
			}

			if (obj[9] != null) {
				Date dataTemp = (Date) obj[9];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[13] != null) {
				vo.setTamDescricao(((String) obj[13]));
			}

			if (obj[14] != null) {
				vo.setDescricaoEsp(((String) obj[14]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.RECEBIDOS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.A);

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 13ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion13(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentosAtendUrgenciaDAO.obterCensoUnion13(unfSeq, unfSeqMae, data, status,
				obterNumeroDiasCenso(), obterCodigoLeitoBloqueioLimpeza());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setLocal(internacaoFacade.obtemLocalDestinoUrgencia((Integer) obj[10], (Date) obj[9]));
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setDthrLancamento(internacaoFacade.obtemDthrFinalUrgencia((Integer) obj[10], (Date) obj[9]));
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setDthrLancamento(internacaoFacade.obtemDthrFinalTUrgencia((Integer) obj[10], (Date) obj[9]));
			}

			if (obj[10] != null) {
				vo.setInternacaoSeq((Integer) obj[10]);
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[13] != null) {
				vo.setTamDescricao(((String) obj[13]));
			}

			if (obj[14] != null) {
				vo.setDescricaoEsp(((String) obj[14]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.ENVIADOS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.A);

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 14ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion14(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ahdHospitaisDiaDAO.obterCensoUnion14(unfSeq, unfSeqMae, data, status);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[10], (Integer) obj[9]));
			}

			if (obj[5] != null) {
				vo.setDthrLancamento((Date) obj[5]);
			}

			if (obj[5] != null) {
				Date dataTemp = (Date) obj[5];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			if (obj[8] != null) {
				vo.setInternacaoSeq((Integer) obj[8]);
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[13] != null) {
				vo.setDescConvenio((String) obj[13]);
			}

			if (obj[14] != null) {
				vo.setTamDescricao(((String) obj[14]));
			}

			if (obj[15] != null) {
				vo.setDescricaoEsp(((String) obj[15]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.INTERNACOES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.H);

			if (obj[11] != null) {
				vo.setCspCnvCodigo((Short) obj[11]);
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
					completaCensoVO(vo, data, status);
					lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 15ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion15(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ahdHospitaisDiaDAO.obterCensoUnion15(unfSeq, unfSeqMae, data, status);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[10], (Integer) obj[9]));
			}

			if (obj[5] != null) {
				vo.setDthrLancamento((Date) obj[5]);
			}

			if (obj[11] != null) {
				vo.setDthrLancamentoFinal((Date) obj[11]);
			}

			if (obj[8] != null) {
				vo.setInternacaoSeq((Integer) obj[8]);
			}

			if (obj[11] != null) {
				vo.setDtNascPaciente((Date) obj[11]);
			}

			if (obj[12] != null) {
				vo.setDescConvenio((String) obj[12]);
			}

			if (obj[13] != null) {
				vo.setTamDescricao(((String) obj[13]));
			}

			if (obj[14] != null) {
				vo.setDescricaoEsp(((String) obj[14]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.H);

			if (obj[12] != null) {
				vo.setCspCnvCodigo((Short) obj[12]);
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 16ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion16(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ahdHospitaisDiaDAO.obterCensoUnion16(unfSeq, unfSeqMae, data, status);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[10], (Integer) obj[9]));
			}

			if (obj[11] != null) {
				vo.setDthrLancamento((Date) obj[11]);
			}

			if (obj[11] != null) {
				Date dataTemp = (Date) obj[11];
				Calendar dataAtual = Calendar.getInstance();
				dataAtual.setTime(dataTemp);
				dataAtual.set(Calendar.HOUR_OF_DAY, 23);
				dataAtual.set(Calendar.MINUTE, 59);
				dataAtual.set(Calendar.SECOND, 0);
				dataAtual.set(Calendar.MILLISECOND, 0);
				vo.setDthrLancamentoFinal(dataAtual.getTime());
			}

			if (obj[8] != null) {
				vo.setInternacaoSeq((Integer) obj[8]);
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.ALTAS);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.H);

			if (obj[12] != null) {
				vo.setCspCnvCodigo((Short) obj[12]);
			}

			if (obj[13] != null) {
				vo.setDtNascPaciente((Date) obj[13]);
			}

			if (obj[14] != null) {
				vo.setDescConvenio((String) obj[14]);
			}

			if (obj[15] != null) {
				vo.setTamDescricao(((String) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescricaoEsp(((String) obj[16]));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 17ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion17(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainInternacaoDAO.obterCensoUnion17(unfSeq, unfSeqMae, data, status);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null) {
				vo.setDthrLancamento(internacaoFacade.buscaDthrFinalInternacao((Integer) obj[9]));
			}

			if (obj[9] != null) {
				vo.setInternacaoSeq((Integer) obj[9]);
			}

			if (obj[10] != null && obj[11] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[11], (Integer) obj[10]));
			}

			if (obj[2] != null) {
				if (((String) obj[2]).length() <= 4) {
					vo.setQrtoLto(StringUtils.leftPad((String) obj[2], 4, "0") + " ");
				} else {
					vo.setQrtoLto((String) obj[2]);
				}
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[9] != null) {
				vo.setLocal(internacaoFacade.obtemOrigemMovimentacao(((Integer) obj[9])));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (obj[13] != null) {
				vo.setDtNascPaciente((Date) obj[13]);
			}

			if (obj[14] != null) {
				vo.setDescConvenio((String) obj[14]);
			}

			if (obj[15] != null) {
				vo.setTamDescricao(((String) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescricaoEsp(((String) obj[16]));
			}

			if (obj[17] != null) {
				vo.setEstadoSaude(buscaEstadoSaudeAtual(((Integer) obj[17]), data));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.I);

			if (obj[12] != null) {
				vo.setCspCnvCodigo(((Short) obj[12]));
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	private String buscaEstadoSaudeAtual(Integer atdSeq, Date dataValida) {
		// System.out.println("atdSeq: " + atdSeq + " e dataValida: " +
		// dataValida);

		// busca atendimento de uma internação
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);

		if (aghAtendimentos != null) {
			AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();
			aghUnidadesFuncionais = aghAtendimentos.getUnidadeFuncional();

			// se unidade funcional possui indicador de CTI - então busca estado
			// de saúde
			// if
			// (DominioSimNao.S.equals(aghUnidadesFuncionais.getIndUnidCti())) {
			// regra acima alterada pela melhoria #20924

			if (this.internacaoFacade.verificarCaracteristicaUnidadeFuncional(aghUnidadesFuncionais.getSeq(),
					ConstanteAghCaractUnidFuncionais.INFORMA_ESTADO_SAUDE)) {

				Calendar auxDataValida = Calendar.getInstance();

				if (dataValida != null) {
					auxDataValida.setTime(dataValida);
					auxDataValida.set(Calendar.HOUR_OF_DAY, 24);
				}

				MamTipoEstadoPaciente estadoPaciente = ambulatorioFacade.obterEstadoAtual(atdSeq,
						auxDataValida.getTime());

				if (estadoPaciente != null && estadoPaciente.getTitulo() != null) {
					return estadoPaciente.getDescricao();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 18ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private List<VAinCensoVO> obterCriteriaCensoUnion18(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainLeitosDAO.obterCensoUnion18(unfSeq, unfSeqMae, data, status,
				obterCodigoLeitoOcupado(), obterCodigoLeitoAlta());

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			vo.setOrigemNomeSituacao(true);

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setNomeSituacao((String) obj[3]);
			}

			if (obj[2] != null) {
				vo.setDthrLancamento(internacaoFacade.buscaDthrLeito((String) obj[2]));
			}

			if (obj[2] != null) {
				vo.setQrtoLto((String) obj[2]);
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(((DominioMovimentoLeito) obj[4]));

			vo.setTipo(DominioTipoCensoDiarioPacientes.L);

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 19ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion19(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = aghuFacade.obterCensoUnion19(unfSeq, unfSeqMae, data, status);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[5] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[6] != null) {
				vo.setDataInternacao((Date) obj[6]);
			}

			if (obj[7] != null) {
				vo.setSiglaEsp((String) obj[7]);
			}

			if (obj[8] != null) {
				vo.setTamCodigo((String) obj[8]);
			}

			if (obj[9] != null) {
				vo.setDthrLancamento(internacaoFacade.buscaDthrAtendimentoUrgencia((Integer) obj[9]));
			}

			if (obj[9] != null) {
				vo.setInternacaoSeq((Integer) obj[9]);
			}

			if (obj[2] != null) {
				vo.setQrtoLto((String) obj[2]);
			} else {
				vo.setQrtoLto(" ");
			}

			if (obj[9] != null) {
				vo.setLocal(internacaoFacade.obtemOrigemAtendimentoUrgencia(((Integer) obj[9])));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (obj[10] != null) {
				vo.setDtNascPaciente((Date) obj[10]);
			}

			if (obj[11] != null) {
				vo.setTamDescricao(((String) obj[11]));
			}

			if (obj[12] != null) {
				vo.setDescricaoEsp(((String) obj[12]));
			}

			if (obj[13] != null) {
				vo.setEstadoSaude(buscaEstadoSaudeAtual(((Integer) obj[13]), data));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.A);

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

	/**
	 * ORADB VIEW V_AIN_CENSO Este método implementa a query da 20ª UNION da
	 * view V_AIN_CENSO.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnion20(Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ahdHospitaisDiaDAO.obterCensoUnion20(unfSeq, unfSeqMae, data, status);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		while (it.hasNext()) {
			boolean adicionar = true;
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
			}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[3] != null) {
				vo.setPacCodigo((Integer) obj[4]);
			}

			if (obj[4] != null) {
				vo.setNomeSituacao((String) obj[5]);
			}

			if (obj[5] != null) {
				vo.setDataInternacao((Date) obj[5]);
			}

			if (obj[6] != null) {
				vo.setSiglaEsp((String) obj[6]);
			}

			if (obj[7] != null) {
				vo.setTamCodigo((String) obj[7]);
			}

			if (obj[9] != null && obj[10] != null) {
				vo.setNomeMedico(pesquisaInternacaoRN.buscarNomeUsual((Short) obj[10], (Integer) obj[9]));
			}

			if (obj[5] != null) {
				vo.setDthrLancamento((Date) obj[5]);
			}

			if (obj[13] != null) {
				vo.setDthrLancamentoFinal((Date) obj[13]);
			}

			if (obj[8] != null) {
				vo.setInternacaoSeq((Integer) obj[8]);
			}

			if (obj[12] != null) {
				vo.setDtNascPaciente((Date) obj[12]);
			}

			if (obj[14] != null) {
				vo.setDescConvenio((String) obj[14]);
			}

			if (obj[15] != null) {
				vo.setTamDescricao(((String) obj[15]));
			}

			if (obj[16] != null) {
				vo.setDescricaoEsp(((String) obj[16]));
			}

			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);

			vo.setGrupoMvtoLeito(DominioMovimentoLeito.O);

			vo.setTipo(DominioTipoCensoDiarioPacientes.H);

			if (obj[11] != null) {
				vo.setCspCnvCodigo((Short) obj[11]);
			}

			if (obj[17] != null) {
				vo.setEstadoSaude(buscaEstadoSaudeAtual(((Integer) obj[17]), data));
			}

			if (data != null && (data.before(DateUtil.truncaData(new Date()))
					|| !status.equals(DominioSituacaoUnidadeFuncional.PACIENTES))) {
				dataPesquisaUltimaHora(data);

				Date dtLancFinal = new Date();
				if (vo.getDthrLancamentoFinal() == null) {
					Calendar dataLancFinal = Calendar.getInstance();
					dataLancFinal.set(Calendar.DATE, 31);
					dataLancFinal.set(Calendar.MONTH, 11);
					dataLancFinal.set(Calendar.YEAR, 2099);
					dataLancFinal.set(Calendar.HOUR_OF_DAY, 0);
					dataLancFinal.set(Calendar.MINUTE, 0);
					dataLancFinal.set(Calendar.SECOND, 0);
					dataLancFinal.set(Calendar.MILLISECOND, 0);
					dtLancFinal.setTime(dataLancFinal.getTime().getTime());

				} else {
					dtLancFinal.setTime(vo.getDthrLancamentoFinal().getTime());
				}

				if (!((data.after(vo.getDthrLancamento()) || data.equals(vo.getDthrLancamento()))
						&& (data.before(dtLancFinal) || data.equals(dtLancFinal)))) {
					adicionar = false;
				}
			}

			if (status != null) {
				if (!status.equals(vo.getStatus())) {
					adicionar = false;
				}
			}

			if (adicionar) {
				completaCensoVO(vo, data, status);
				lista.add(vo);
			}
		}

		return lista;
	}

 	/**
	 * Este é para garantir que no censo diário estejam todos os leitos da
	 * unidade mesmo que estes não tenham movimento.
	 * 
	 * @return listaObjetos
	 * @throws ApplicationBusinessException
	 */
	//@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private List<VAinCensoVO> obterCriteriaCensoUnionAll(Short unfSeq,
			Short unfSeqMae, Date dataPesquisa,
			DominioSituacaoUnidadeFuncional status)
			throws ApplicationBusinessException {

		List<Object[]> listaObjetos = ainMovimentoInternacaoDAO.obterCensoUnionAll(unfSeq, unfSeqMae);

		List<VAinCensoVO> lista = new ArrayList<VAinCensoVO>();
		Iterator<Object[]> it = listaObjetos.listIterator();

		Integer janela = obterNumeroDiasCenso();

		while (it.hasNext()) {
			Object[] obj = it.next();
			VAinCensoVO vo = new VAinCensoVO();

			if (obj[0] != null) {
				vo.setUnfSeq((Short) obj[0]);
		}

			if (obj[1] != null) {
				vo.setUnfMaeSeq((Short) obj[1]);
			}

			if (obj[2] != null) {
				vo.setQrtoLto((String) obj[2]);
			}
			if (obj[3] != null) {
				vo.setDthrLancamento((Date) obj[3]);
			}

			vo.setNomeSituacao(new String("LEITO SEM MOVIMENTO - JANELA ["+ janela + "]"));
			vo.setStatus(DominioSituacaoUnidadeFuncional.PACIENTES);
			vo.setGrupoMvtoLeito(DominioMovimentoLeito.I);
			vo.setTipo(DominioTipoCensoDiarioPacientes.I);
			vo.setDthrLancamento(obterData(dataPesquisa, -janela));

			lista.add(vo);
		}
		return lista;
	}
	
	private Date obterData(final Date data, final Integer numDiasCalculo) {
		final Calendar dataCalculo = Calendar.getInstance();

		dataCalculo.setTime(data);
		dataCalculo.add(Calendar.DATE, numDiasCalculo);
		dataCalculo.set(Calendar.HOUR_OF_DAY, 0);
		dataCalculo.set(Calendar.MINUTE, 0);
		dataCalculo.set(Calendar.SECOND, 0);
		dataCalculo.set(Calendar.MILLISECOND, 0);

		return dataCalculo.getTime();
	}	
	
	/**
	 * Obtem a observacaoCenso de uma internacao com data de criacao menor ou
	 * igual a data passada como parametro e também a partir do id da
	 * internacao.
	 * 
	 * @param intSeq
	 * @return AinObservacoesCenso
	 */
	@Secure("#{s:hasPermission('observacoesCensoDiario','pesquisar')}")
	public AinObservacoesCenso obterObservacaoDaInternacao(Integer intSeq, Date data) {

		AinObservacoesCenso obs = ainObservacoesCensoDAO.obterObservacaoDaInternacao(intSeq, data);

		Calendar auxCalData = Calendar.getInstance();
		Calendar auxCal = Calendar.getInstance();

		if (data != null && obs != null) {
			auxCalData.setTime(data);
			auxCalData.set(Calendar.HOUR_OF_DAY, 0);
			auxCalData.set(Calendar.MINUTE, 0);
			auxCalData.set(Calendar.SECOND, 0);
			auxCalData.set(Calendar.MILLISECOND, 0);
			auxCalData.add(Calendar.DAY_OF_MONTH, 1);

			auxCal.setTime(obs.getCriadoEm());
			auxCal.set(Calendar.HOUR_OF_DAY, 0);
			auxCal.set(Calendar.MINUTE, 0);
			auxCal.set(Calendar.SECOND, 0);
			auxCal.set(Calendar.MILLISECOND, 0);

			if (auxCal.before(auxCalData)) {
				return obs;
			}
		}
		return null;
	}

	public void excluirObservacaoDaInternacao(AinObservacoesCenso observacao) {
		AinObservacoesCenso obs = ainObservacoesCensoDAO.obterPorChavePrimaria(observacao.getId());
		if (obs != null) {
			ainObservacoesCensoDAO.remover(obs);
			ainObservacoesCensoDAO.flush();
		}
	}

	public Integer calcularIdadeDaData(Date data) {
		// Data de hoje.
		Calendar agora = Calendar.getInstance();
		int ano, mes, dia = 0;

		// Data de nascimento.
		Calendar nascimento = Calendar.getInstance();
		int anoNasc, mesNasc, diaNasc = 0;

		// Idade.
		int idade = 0;

		if (data != null) {
			nascimento.setTime(data);

			ano = agora.get(Calendar.YEAR);
			mes = agora.get(Calendar.MONTH) + 1;
			dia = agora.get(Calendar.DAY_OF_MONTH);

			anoNasc = nascimento.get(Calendar.YEAR);
			mesNasc = nascimento.get(Calendar.MONTH) + 1;
			diaNasc = nascimento.get(Calendar.DAY_OF_MONTH);

			idade = ano - anoNasc;

			// Calculando diferencas de mes e dia.
			if ((mes <= mesNasc) && (dia < diaNasc)) {
				idade--;
			}
			// idade "negativa".
			if (idade < 0) {
				idade = 0;
			}
		}
		return idade;
	}

	/**
	 * @ORADB ainc_verifica_docs
	 * 
	 *        Verifica se uma determinada internacao possui documentos
	 *        pendentes.
	 * 
	 * @param intSeq
	 *            - id da internacao
	 * @return true - se existirem docs pendentes
	 */
	public boolean verificarDocumentosPendentes(Integer intSeq) {
		Long count = ainDocsInternacaoDAO.countDocumentosPendentes(intSeq);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Inclui ou edita uma observacao.
	 * 
	 * @param observacaoCenso
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('observacoesCensoDiario','alterar')}")
	public void persistirObservacao(AinObservacoesCenso observacaoCenso) throws ApplicationBusinessException {
		try {
			if (observacaoCenso.getId() == null || observacaoCenso.getId().getSeq() == null) {
				// inclusao
				this.incluirObservacao(observacaoCenso);
			} else {
				// edicao
				this.editarObservacao(observacaoCenso);
			}
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(PesquisaCensoDiarioPacienteONExceptionCode.ERRO_PERSISTIR_OBS);
		}
	}

	/**
	 * Inclui uma nova observacao.
	 * 
	 * @param observacaoCenso
	 * @throws ApplicationBusinessException
	 */
	private void incluirObservacao(AinObservacoesCenso observacaoCenso) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		observacaoCenso.setCriadoEm(new Date());
		observacaoCenso.setSerMatricula(servidorLogado.getId().getMatricula());
		observacaoCenso.setSerVinCodigo(servidorLogado.getId().getVinCodigo());

		AinObservacoesCensoDAO ainObservacoesCensoDAO = this.ainObservacoesCensoDAO;
		ainObservacoesCensoDAO.persistir(observacaoCenso);
		ainObservacoesCensoDAO.flush();
	}

	/**
	 * Edita uma observacao ja existente.
	 * 
	 * @param observacaoCenso
	 * @throws ApplicationBusinessException
	 */
	private void editarObservacao(AinObservacoesCenso observacaoCenso) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		observacaoCenso.setAlteradoEm(new Date());
		observacaoCenso.setSerMatriculaAltera(servidorLogado.getId().getMatricula());
		observacaoCenso.setSerVinCodigoAltera(servidorLogado.getId().getVinCodigo());

		AinObservacoesCensoDAO ainObservacoesCensoDAO = this.ainObservacoesCensoDAO;
		ainObservacoesCensoDAO.atualizar(observacaoCenso);
		ainObservacoesCensoDAO.flush();
	}

	public Boolean pacienteNotifGMR(final Integer aipPacientesCodigo) {
		return controleInfeccaoFacade.verificarNotificacaoGmrPorCodigo(aipPacientesCodigo);
	}

	/**
	 * Função pacienteNotifGMR Verifica se o paciente possui notificação de
	 * germe multirresistente. Chamar função: MCIC_PAC_NOTIF_GMR
	 * 
	 * @param Integer
	 *            aipPacientesCodigo
	 * @param String
	 *            sLogin
	 * 
	 * @throws ApplicationBusinessException
	 */
	public boolean pacienteNotifGMR(final Integer aipPacientesCodigo, String sLogin)
			throws ApplicationBusinessException {

		// Solucao temporaria para as regras do HCPA. Nao executa esse codigo em
		// outros HU's.
		if (!isHCPA() || !ainInternacaoDAO.isOracle()) {
			return false;
		}

		final List<Boolean> result = new ArrayList<Boolean>();

		AghWork work = new AghWork(sLogin) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				final String nomeObjeto = EsquemasOracleEnum.AGH + "."
						+ ObjetosBancoOracleEnum.MCIC_PAC_NOTIF_GMR.toString();
				CallableStatement cs = null;
				try {
					cs = connection.prepareCall("{? = call " + nomeObjeto + "(?)}");
					// Registro de parametro IN
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, aipPacientesCodigo);
					// Registro de parametro OUT
					cs.registerOutParameter(1, Types.VARCHAR);
					cs.execute();

					boolean resultado;

					if (cs.getString(1).toString().equals("S")) {
						resultado = true;
					} else {
						resultado = false;
					}
					result.add(resultado);

				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};

		try {
			ahdHospitaisDiaDAO.doWork(work);

		} catch (Exception e) {
			logError(String.format(
					"Erro ao executar a funcao MCIC_PAC_NOTIF_GMR, que verifica se o paciente possui notificacao de germe multirresistente.",
					sLogin, e.getMessage()), e);
			return false;
		}

		if (work.getException() != null) {
			logError(String.format(
					"Erro ao executar a funcao MCIC_PAC_NOTIF_GMR, que verifica se o paciente possui notificacao de germe multirresistente.",
					sLogin, work.getException().getMessage()), work.getException());
			return false;
		}

		return result.get(0);
	}

	private void completaCensoVO(VAinCensoVO vAinCensoVO, Date dataPesquisa, DominioSituacaoUnidadeFuncional status)
			throws ApplicationBusinessException {

		vAinCensoVO.setEstiloColunaTempo(this.calcularEstiloTempo(vAinCensoVO.getInternacaoSeq(),
				vAinCensoVO.getDataInternacao(), vAinCensoVO.getTipo(), dataPesquisa, status));
		vAinCensoVO.setEstiloColunaProntuario(
				this.calcularEstiloDoProntuario(vAinCensoVO.getInternacaoSeq(), dataPesquisa));
		vAinCensoVO.setEstiloColunaQrto(vAinCensoVO.getDtNascPaciente() == null ? false
				: this.calcularIdadeDaData(vAinCensoVO.getDtNascPaciente()) < 18 ? true : false);
		vAinCensoVO.setTempo(this.calcularTempo(vAinCensoVO.getDataInternacao(), dataPesquisa));
		vAinCensoVO.setExibirBotaoCadastroDePacientes(this.exibirBotaoCadastroDePacientes(vAinCensoVO.getProntuario()));
		vAinCensoVO.setExibirBotaoExtrato(this.exibirBotaoExtrato(vAinCensoVO.getTipo()));
		vAinCensoVO.setExibirBotaoTransferencia(
				this.exibirBotaoTransferencia(vAinCensoVO.getTipo(), dataPesquisa, status));
		vAinCensoVO.setExibirBotaoInternacao(this.exibirBotaoInternacao(vAinCensoVO.getTipo(),
				vAinCensoVO.getGrupoMvtoLeito(), vAinCensoVO.getTamCodigo()));
		vAinCensoVO.setExibirBotaoAlta(this.exibirBotaoAlta(vAinCensoVO.getTipo()));
		vAinCensoVO.setLabelBotaoInternacao(
				this.definirLabelBotaoInternacao(vAinCensoVO.getTipo(), vAinCensoVO.getGrupoMvtoLeito()));
		vAinCensoVO.setIconeBotaoInternacao(
				this.definirIconeBotaoInternacao(vAinCensoVO.getTipo(), vAinCensoVO.getGrupoMvtoLeito()));
		vAinCensoVO.setEstadoSaude(vAinCensoVO.getEstadoSaude());
		if (vAinCensoVO.getPacCodigo() != null) {
			AipPacientes paciente = pacienteFacade.obterPaciente(vAinCensoVO.getPacCodigo());
			vAinCensoVO.setNroCartaoSaude(paciente.getNroCartaoSaude());
		}

		// atualiza previsão de alta nas próximas XX horas
		vAinCensoVO.setPrevisaoDeAltaNasProximasHoras(
				this.getVerificaPrevisaoDeAltaNasProximasHoras(vAinCensoVO.getInternacaoSeq()));
		vAinCensoVO.setPacienteNotifGMR(this.pesquisaInternacaoFacade.pacienteNotifGMR(vAinCensoVO.getPacCodigo()));
		sinalizarAltaEpacienteGMR(vAinCensoVO);
	}

	/**
	 * AINP_CALCULA_TEMPO (PLL) Define o estilo de apresentacao da coluna tempo
	 * na tela. Pode ser com fundo azul, sublinhado ou normal.
	 * 
	 * @param intSeq
	 * @param dataIntenacao
	 * @param data
	 * @param status
	 */
	private Integer calcularEstiloTempo(Integer intSeq, Date dataIntenacao, DominioTipoCensoDiarioPacientes tipo,
			Date dataPesquisa, DominioSituacaoUnidadeFuncional status) {
		VerificaPermissaoVO voRetorno = new VerificaPermissaoVO();
		Integer tempo = 0;
		if (dataIntenacao != null) {
			Calendar auxCalData = Calendar.getInstance();
			Calendar auxCalInternacao = Calendar.getInstance();
			auxCalData.setTime(dataPesquisa);
			auxCalData.set(Calendar.HOUR_OF_DAY, 0);
			auxCalData.set(Calendar.MINUTE, 0);
			auxCalData.set(Calendar.SECOND, 0);
			auxCalData.set(Calendar.MILLISECOND, 0);

			auxCalInternacao.setTime(dataIntenacao);
			auxCalInternacao.set(Calendar.HOUR_OF_DAY, 0);
			auxCalInternacao.set(Calendar.MINUTE, 0);
			auxCalInternacao.set(Calendar.SECOND, 0);
			auxCalInternacao.set(Calendar.MILLISECOND, 0);

			tempo = DateUtil.diffInDaysInteger(auxCalData.getTime(), auxCalInternacao.getTime()).intValue();
		}
		this.internacaoFacade.verificarPermissaoAltaPaciente(intSeq, dataPesquisa, voRetorno);
		boolean docsPendentes = this.pesquisaInternacaoFacade.verificarDocumentosPendentes(intSeq);
		if (DominioSimNao.S.equals(voRetorno.getPermMaior())
				|| (tempo > 1 && DominioTipoCensoDiarioPacientes.A.equals(tipo)
						&& DominioSituacaoUnidadeFuncional.PACIENTES.equals(status))) {
			if (docsPendentes && DominioSituacaoUnidadeFuncional.PACIENTES.equals(status)) {
				return 1;// Normal
			} else {
				return 2;// Fundo azul
			}
		} else {
			if (docsPendentes && DominioSituacaoUnidadeFuncional.PACIENTES.equals(status)) {
				return 3;// sublinhado
			}
		}
		return 1;
	}

	/**
	 * Calcula a diferenca de tempo em dias entre a data de internacao e a data
	 * informada na tela.
	 * 
	 * @param dataIntenacao
	 * @return
	 */
	private Integer calcularTempo(Date dataIntenacao, Date dataPesquisa) {
		Integer tempo = 0;
		if (dataIntenacao != null) {
			Calendar auxCalData = Calendar.getInstance();
			Calendar auxCalInternacao = Calendar.getInstance();
			auxCalData.setTime(dataPesquisa);
			auxCalData.set(Calendar.HOUR_OF_DAY, 0);
			auxCalData.set(Calendar.MINUTE, 0);
			auxCalData.set(Calendar.SECOND, 0);
			auxCalData.set(Calendar.MILLISECOND, 0);

			auxCalInternacao.setTime(dataIntenacao);
			auxCalInternacao.set(Calendar.HOUR_OF_DAY, 0);
			auxCalInternacao.set(Calendar.MINUTE, 0);
			auxCalInternacao.set(Calendar.SECOND, 0);
			auxCalInternacao.set(Calendar.MILLISECOND, 0);

			tempo = DateUtil.diffInDaysInteger(auxCalData.getTime(), auxCalInternacao.getTime()).intValue();
		}
		return tempo;
	}

	/**
	 * Determina o estilo da coluna prontuario: Amarelo, caso a internacao tenha
	 * observacao.
	 * 
	 * @param intSeq
	 */
	private boolean calcularEstiloDoProntuario(Integer intSeq, Date dataPesquisa) {
		boolean retorno = false;
		if (this.pesquisaInternacaoFacade.obterObservacaoDaInternacao(intSeq, dataPesquisa) != null) {
			// retorno = "background-color:yellow;";
			retorno = true;
		}
		return retorno;
	}

	// #######################################//
	// ### CONTROLE DE EXIBIÇÃO DOS BOTOES ###//
	// #######################################//
	/**
	 * Controla a exibicao do botao 'ALTA'
	 */
	private boolean exibirBotaoAlta(DominioTipoCensoDiarioPacientes tipo) {
		boolean retorno = false;

		// #### RETIRAR ESTE IF QUANDO A TELA DE ALTA DO PACIENTE EM OBSERVACAO
		// ESTIVER PRONTA!!!#####
		if (!DominioTipoCensoDiarioPacientes.I.equals(tipo)) {
			return false;
		}

		if (tipo != null
				&& (DominioTipoCensoDiarioPacientes.I.equals(tipo) || DominioTipoCensoDiarioPacientes.A.equals(tipo))) {
			retorno = true;
		}
		return retorno;
	}

	/**
	 * Controla a exibicao do botao 'INTERNACAO'
	 */
	private boolean exibirBotaoInternacao(DominioTipoCensoDiarioPacientes tipo, DominioMovimentoLeito grupoMvtoLeito,
			String codTipoAltaMedica) {
		boolean retorno = false;

		// ### RETIRAR O IF ABAIXO QUANDO A TELA DE
		// IngressarPacienteSalaObservacao FOR IMPLEMENTADA - VER METODO
		// redirecionarBotaoInternacao
		// ### RETIRAR TAMBEM O ARGUMENTO "String codTipoAltaMedica" DESTE
		// METODO, POIS É USADO TEMPORARIAMENTE SÓ PARA ESTA VALIDACAO
		// ### ESTE IF FOI IMPLEMENTADO SOMENTE PARA NAO EXIBIR O BOTAO NA GRID
		// ENQUANTO A TELA NAO FOR DESENVOLVIDA
		if (!DominioTipoCensoDiarioPacientes.L.equals(tipo) && !DominioTipoCensoDiarioPacientes.I.equals(tipo)
				&& StringUtils.isNotBlank(codTipoAltaMedica)) {
			return false;
		}

		if (tipo != null
				&& (DominioTipoCensoDiarioPacientes.I.equals(tipo) || DominioTipoCensoDiarioPacientes.A.equals(tipo))
				|| ((DominioTipoCensoDiarioPacientes.L.equals(tipo) && (DominioMovimentoLeito.L.equals(grupoMvtoLeito)
						|| DominioMovimentoLeito.D.equals(grupoMvtoLeito)
						|| DominioMovimentoLeito.BL.equals(grupoMvtoLeito)
						|| DominioMovimentoLeito.B.equals(grupoMvtoLeito)
						|| DominioMovimentoLeito.R.equals(grupoMvtoLeito)
						|| DominioMovimentoLeito.BI.equals(grupoMvtoLeito))))) {
			retorno = true;
		}
		return retorno;
	}

	private enum LabelBotaoInternacaoCode {
		LABEL_INTERNACAO, LABEL_BLOQUEIO, LABEL_LIBERACAO;
	}

	/**
	 * Controla a definicao do label do botao 'INTERNACAO '
	 */
	private String definirLabelBotaoInternacao(DominioTipoCensoDiarioPacientes tipo,
			DominioMovimentoLeito grupoMvtoLeito) {
		String retorno = "";

		if (DominioTipoCensoDiarioPacientes.I.equals(tipo) || DominioTipoCensoDiarioPacientes.A.equals(tipo)) {
			retorno = LabelBotaoInternacaoCode.LABEL_INTERNACAO.toString();
		} else {
			if (DominioTipoCensoDiarioPacientes.L.equals(tipo)) {
				if (DominioMovimentoLeito.L.equals(grupoMvtoLeito)) {
					retorno = LabelBotaoInternacaoCode.LABEL_BLOQUEIO.toString();
				} else {
					retorno = LabelBotaoInternacaoCode.LABEL_LIBERACAO.toString();
				}
			}
		}
		return retorno;
	}

	/**
	 * Controla a definicao do ICONE do botao 'INTERNACAO '
	 */
	private String definirIconeBotaoInternacao(DominioTipoCensoDiarioPacientes tipo,
			DominioMovimentoLeito grupoMvtoLeito) {
		String retorno = "";

		if (DominioTipoCensoDiarioPacientes.I.equals(tipo) || DominioTipoCensoDiarioPacientes.A.equals(tipo)) {
			retorno = "silk-icon silk-internacao";// Internacao
		} else {
			if (DominioTipoCensoDiarioPacientes.L.equals(tipo)) {
				if (DominioMovimentoLeito.L.equals(grupoMvtoLeito)) {
					retorno = "silk-icon silk-flag-red";// Bloquear leito
				} else {
					retorno = "silk-icon silk-tick";// Liberar Leito
				}
			}
		}
		return retorno;
	}

	/**
	 * Controla a exibicao do botao 'TRANSFERENCIA'
	 */
	private boolean exibirBotaoTransferencia(DominioTipoCensoDiarioPacientes tipo, Date dataPesquisa,
			DominioSituacaoUnidadeFuncional status) {
		boolean retorno = false;
		Calendar auxCalData = Calendar.getInstance();
		Calendar auxCal = Calendar.getInstance();

		if (dataPesquisa != null) {
			auxCalData.setTime(dataPesquisa);
			auxCalData.set(Calendar.HOUR_OF_DAY, 0);
			auxCalData.set(Calendar.MINUTE, 0);
			auxCalData.set(Calendar.SECOND, 0);
			auxCalData.set(Calendar.MILLISECOND, 0);

			auxCal.setTime(new Date());
			auxCal.set(Calendar.HOUR_OF_DAY, 0);
			auxCal.set(Calendar.MINUTE, 0);
			auxCal.set(Calendar.SECOND, 0);
			auxCal.set(Calendar.MILLISECOND, 0);

			if (auxCalData.compareTo(auxCal) == 0 && tipo != null && DominioTipoCensoDiarioPacientes.I.equals(tipo)
					&& DominioSituacaoUnidadeFuncional.PACIENTES.equals(status)) {
				retorno = true;
			}
		}
		return retorno;
	}

	/**
	 * Controla a exibicao do botao 'EXTRATO'
	 */
	private boolean exibirBotaoExtrato(DominioTipoCensoDiarioPacientes tipo) {
		boolean retorno = false;
		if (tipo != null) {
			retorno = true;
		}
		return retorno;
	}

	/**
	 * Controla a exibicao do botao 'CADASTRO DE PACIENTES'
	 */
	private boolean exibirBotaoCadastroDePacientes(Integer prontuario) {
		boolean retorno = false;
		if (prontuario != null) {
			retorno = true;
		}
		return retorno;
	}

	private String obterTextoPrevisaoDeAltaNasProximasHoras() throws ApplicationBusinessException {

		AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HORAS_CONTROLE_PREV_ALTA);

		StringBuffer descricaoAtualizacao = new StringBuffer(80);
		descricaoAtualizacao.append("Fundo Verde-Claro indica paciente com Previsão de Altas nas Próximas ")
				.append(param.getVlrNumerico().toString()).append(" horas");

		return descricaoAtualizacao.toString();

	}

	/**
	 * Verifica se o paciente dará alta nas proximas horas . parametro
	 * :p_horas_controle_prev_alta=48 horas
	 * 
	 * @author andremachado
	 * @param intSeq
	 * @return
	 */
	public boolean getVerificaPrevisaoDeAltaNasProximasHoras(Integer intSeq) throws ApplicationBusinessException {
		boolean retorno = false;

		// busca atendimento de uma internação
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos = this.aghuFacade.obterAtendimentoPorIntSeq(intSeq);

		// Verifica parâmetro P_USA_CONTROLE_PREV_ALTA = 'S'
		// Se parâmetro P_USA_CONTROLE_PREV_ALTA = 'S' então
		// Verifica se paciente terá Alta, nas próximas horas
		// chamando a função MPMC_PAC_PRV_ALT_48H(v_atd_seq);
		// se a função retornar true então pinta de verde a coluna nome_situacao

		if (aghAtendimentos != null) {
			AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_USA_CONTROLE_PREV_ALTA);

			if (param.getVlrTexto().equals("S")) {

				retorno = prescricaoMedicaFacade.verificaPrevisaoAltaProxima(aghAtendimentos);

			}
		}

		return retorno;
	}

	private void sinalizarAltaEpacienteGMR(VAinCensoVO vAinCensoVO) throws ApplicationBusinessException {
		boolean bPrevisaoDeAltaNasProximasHoras = vAinCensoVO.isPrevisaoDeAltaNasProximasHoras();
		boolean bPacienteNotifGMR = vAinCensoVO.isPacienteNotifGMR();

		if (bPacienteNotifGMR) {
			vAinCensoVO.setDescPacienteNotifGMR(
					"Fundo Ciano indica paciente sinalizado portador de germe multirresistente");
		}

		if (bPrevisaoDeAltaNasProximasHoras) {
			vAinCensoVO.setDescPrevisaoDeAltaNasProximasHoras(obterTextoPrevisaoDeAltaNasProximasHoras());
		}
	}

}