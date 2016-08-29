package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.farmacia.dispensacao.business.DispensaMedicamentoRN.DispensaMedicamentoRNExceptionCode;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.DispensacaoFarmaciaVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensa2VO;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class DispensaMedicamentoON extends BaseBusiness {

	private static final String _HIFEN_ = " - ";
	
	@EJB
	private DispensaMedicamentoRN dispensaMedicamentoRN;
	
	private static final Log LOG = LogFactory.getLog(DispensaMedicamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1875491431085828731L;

	private enum DispensaMedicamentoONExceptionCode implements
	BusinessExceptionCode {
		UNIDADE_FARMACIA_NAO_CADASTRADA
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	/**
	 * @ORADB - IMPRIME_MEDICAM_FARMACIA_DISP
	 * 
	 *  
	 */
	public DispensacaoFarmaciaVO imprimirMedicamentoFarmaciaDispensa(DispensacaoFarmaciaVO dispensacaoFarmaciaVO, List<Short> unidadeFuncionaisFarmaciaMe, Short farmPadrao) throws BaseException {
		
		ArrayList<AghUnidadesFuncionais> listaUnidadesFuncional = (ArrayList<AghUnidadesFuncionais>) getAghuFacade().listarUnidadeFarmacia();
		
		if (listaUnidadesFuncional.isEmpty()){
			throw new ApplicationBusinessException(
					DispensaMedicamentoONExceptionCode.UNIDADE_FARMACIA_NAO_CADASTRADA);
		}
		ArrayList<String> farmacias = new ArrayList<String>();

		// Old: Caminho de rede -> ImpImpressora.
		// ArrayList<String> impressoras = new ArrayList<String>();
		Set<ImpImpressora> impressorasCups = new HashSet<ImpImpressora>();
		for(AghUnidadesFuncionais unidadeFuncional: listaUnidadesFuncional){
			Short unidadeFuncionalSeq = unidadeFuncional.getSeq();
			Byte nroViasPme;
			if (unidadeFuncional.getNroViasPme() == null) {
				nroViasPme = Byte.valueOf("2");
			} else {
				nroViasPme = unidadeFuncional.getNroViasPme();
			}
			
			if(this.verificarFarmaciaMe(unidadeFuncionaisFarmaciaMe, unidadeFuncionalSeq)) {

				if (nroViasPme == 2) {
					AghParametros paramNroCopiasFarm = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NRO_COPIAS_FARM);
					nroViasPme = paramNroCopiasFarm.getVlrNumerico().byteValue();
				}
				
				ImpImpressora impressora = this.relatorioDispensacaoFarmacia(unidadeFuncionalSeq, nroViasPme,
						farmPadrao == unidadeFuncionalSeq, dispensacaoFarmaciaVO.getPrescricaoMdto());
				
				if (impressora != null) {
					farmacias.add(unidadeFuncionalSeq.toString());
					impressorasCups.add(impressora);
				}
			}
		}
	
		dispensacaoFarmaciaVO.setFarmacias(farmacias);
		dispensacaoFarmaciaVO.setImpressorasCups(impressorasCups);
		return dispensacaoFarmaciaVO;
	}
	
	public List<AghUnidadesFuncionais> listarFarmacias(){
		return (ArrayList<AghUnidadesFuncionais>) getAghuFacade().listarUnidadeFarmacia();
	}
	
	/**
	 * Método que retorna <code>ImpImpressora</code> por Und. Funcionaal para
	 * impressão de Dispensação de Farmácia.
	 * 
	 * @ORADB CHAMA_REPORT_MEDICAM_FARM_DISP : verificar
	 * @param unfSeq
	 * @param nroViasPme
	 * @param padrao
	 * @param prescricaoMdto
	 * @return
	 * @throws BaseException
	 */
	public ImpImpressora relatorioDispensacaoFarmacia(Short unfSeq, Byte nroViasPme, Boolean padrao, MpmPrescricaoMdto prescricaoMdto) throws BaseException {
		
	Short farmaciaPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNF_FARM_DISP).getVlrNumerico().shortValue();	
		
		validaPreenchimentoParametroFarmaciaPadrao(farmaciaPadrao);
			
		if (farmaciaPadrao == unfSeq) {
			AghAtendimentos atendimento = prescricaoMdto.getPrescricaoMedica().getAtendimento();
			AghImpressoraPadraoUnids impressoraPadraoUnids = this.getAghuFacade().obterImpressoraPadraoUnidPorAtendimento(atendimento);
			
			if (impressoraPadraoUnids != null) {
				return impressoraPadraoUnids.getImpImpressora();
			} else {
				return this.getCadastrosBasicosInternacaoFacade().obterImpressora(farmaciaPadrao, TipoDocumentoImpressao.PRESCRICAO_MEDICA);
			}
		} else {
			return this.getCadastrosBasicosInternacaoFacade().obterImpressora(farmaciaPadrao, TipoDocumentoImpressao.PRESCRICAO_MEDICA);
		}
	}
	
	public void validaPreenchimentoParametroFarmaciaPadrao(Short farmaciaPadrao) throws BaseException  {
		if(farmaciaPadrao == null){
			throw new ApplicationBusinessException(DispensaMedicamentoRNExceptionCode.MPM_01708); 
		}
	
	}
	
	private List<Short> buscarUnidadeFuncionaisFarmaciaMe(Integer atendimentoSeq, Date dthrInicio, Date dthrFim, Short farmPadrao)  throws ApplicationBusinessException{
		
		List<Short> unidadesFuncionais = new ArrayList<Short>();
		ArrayList<MpmItemPrescricaoMdto> listaItensPrescricaoMdtos = (ArrayList<MpmItemPrescricaoMdto>) this
				.getPrescricaoMedicaFacade().listarItensPrescricaoMedicamentoFarmaciaMe(atendimentoSeq, dthrInicio, dthrFim);
		
		if(listaItensPrescricaoMdtos != null && !listaItensPrescricaoMdtos.isEmpty()){
			for(MpmItemPrescricaoMdto itemPrescricaoMdto: listaItensPrescricaoMdtos){
				unidadesFuncionais.add(this.getDispensaMedicamentoRN().getLocalDispensa2(atendimentoSeq,
						itemPrescricaoMdto.getMedicamento().getMatCodigo(), itemPrescricaoMdto.getDose(),
						itemPrescricaoMdto.getFormaDosagem().getSeq(), farmPadrao));
			}
		}
		
		return unidadesFuncionais;
	}
	
	/**
	 * @ORADB MPMC_VER_FARMACIA_ME
	 * 
	 *  
	 */
	private Boolean verificarFarmaciaMe(List<Short> unidadesFuncionais, Short unidadeFuncionalSeq) throws ApplicationBusinessException {
		return unidadesFuncionais.contains(unidadeFuncionalSeq);
	}
	
	
	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentosConfirmados(
			MpmPrescricaoMedicaId prescricaoMedicaId) throws ApplicationBusinessException {
		if (prescricaoMedicaId == null || prescricaoMedicaId.getAtdSeq() == null
				|| prescricaoMedicaId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscarPrescricaoMedica: parametros de filtro invalido");
		}
		MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaFacade()
				.obterMpmPrescricaoMedicaPorChavePrimaria(prescricaoMedicaId);
		List<MpmPrescricaoMdto> listMedicamentos = this.getPrescricaoMedicaFacade()
				.obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(prescricaoMedicaId,
						prescricaoMedica.getDthrFim(), null);
		return listMedicamentos;
	}
	
	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentos(
			MpmPrescricaoMedicaId prescricaoMedicaId) throws ApplicationBusinessException {
		if (prescricaoMedicaId == null || prescricaoMedicaId.getAtdSeq() == null
				|| prescricaoMedicaId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscarPrescricaoMedica: parametros de filtro invalido");
		}
		MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaFacade()
				.obterMpmPrescricaoMedicaPorChavePrimaria(prescricaoMedicaId);
		List<MpmPrescricaoMdto> listMedicamentos = this.getPrescricaoMedicaFacade()
				.obterListaMedicamentosPrescritos(prescricaoMedicaId,
						prescricaoMedica.getDthrFim(), null);
		return listMedicamentos;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<DispensacaoFarmaciaVO> popularDispensacaoFarmaciaVO(MpmPrescricaoMedicaId prescricaoMedicaId, List<MpmPrescricaoMdto> listaMedicamentos, 
			Boolean alterado) throws BaseException {
		ArrayList<DispensacaoFarmaciaVO> listaMedicamentosVO = new ArrayList<DispensacaoFarmaciaVO>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		listaMedicamentos = this.ordenarMedicamentos(listaMedicamentos);
		
		Short farmPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNF_FARM_DISP).getVlrNumerico().shortValue();
		
		for (MpmPrescricaoMdto prescricaoMedicamento : listaMedicamentos) {
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO = new DispensacaoFarmaciaVO();
			BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO = 
				getPrescricaoMedicaFacade().buscaConselhoProfissionalServidorVO(null);
			MpmPrescricaoMedica prescricaoMedica = this.getPrescricaoMedicaFacade().obterMpmPrescricaoMedicaPorChavePrimaria(prescricaoMedicaId);
			dispensacaoFarmaciaVO.setPrescricaoMdto(prescricaoMedicamento);
			
			List<Short> unidadeFuncionaisFarmaciaMe = buscarUnidadeFuncionaisFarmaciaMe(dispensacaoFarmaciaVO.getPrescricaoMdto().getPrescricaoMedica().getAtendimento().getSeq(), 
					dispensacaoFarmaciaVO.getPrescricaoMdto().getDthrInicio(), dispensacaoFarmaciaVO.getPrescricaoMdto().getDthrFim(), farmPadrao);
			
			if (alterado) {
				Date dthrMovimento = null;
				if(prescricaoMedica.getDthrInicioMvtoPendente() != null){
					dthrMovimento = prescricaoMedica.getDthrInicioMvtoPendente();
				}else{
					dthrMovimento = prescricaoMedica.getDthrMovimento();
				}
				dispensacaoFarmaciaVO = this.imprimirMovimentacaoMededicamentoFarmaciaDispensa(dthrMovimento, dispensacaoFarmaciaVO, unidadeFuncionaisFarmaciaMe, farmPadrao);
			} else {
				dispensacaoFarmaciaVO = this.imprimirMedicamentoFarmaciaDispensa(dispensacaoFarmaciaVO, unidadeFuncionaisFarmaciaMe, farmPadrao);
			}			
			
			for(MpmItemPrescricaoMdto itemPrescricaoMdto: prescricaoMedicamento.getItensPrescricaoMdtos()){
				Short unfSeqFarmacia = this.getDispensaMedicamentoRN().getLocalDispensa2(itemPrescricaoMdto.getId().getPmdAtdSeq(),
						itemPrescricaoMdto.getMedicamento().getMatCodigo(), itemPrescricaoMdto.getDose(),
						itemPrescricaoMdto.getFormaDosagem().getSeq(), farmPadrao);
					
					ImpImpressora impressora = this.relatorioDispensacaoFarmacia(unfSeqFarmacia, null,
							farmPadrao == unfSeqFarmacia, dispensacaoFarmaciaVO.getPrescricaoMdto());
					dispensacaoFarmaciaVO.setImpressoraCups(impressora);
					dispensacaoFarmaciaVO.setFarmacia(unfSeqFarmacia != null ? unfSeqFarmacia.toString() : null);
			}			
			
			populaDispensacaoFarmaciaVONomeConselhoProfissionalServidor(dispensacaoFarmaciaVO, buscaConselhoProfissionalServidorVO);
			populaDispensacaoVOSiglaConselho(dispensacaoFarmaciaVO, buscaConselhoProfissionalServidorVO);
			populaDispensacaoVONumeroRegistroConselho(dispensacaoFarmaciaVO,buscaConselhoProfissionalServidorVO);
				
			if (!alterado){
				String aprazamento = gerarAprazamentoString( this.getPrescricaoMedicaFacade().gerarAprazamento(prescricaoMedica.getId().getAtdSeq(), 
						prescricaoMedica.getId().getSeq(), prescricaoMedicamento.getDthrInicio(), prescricaoMedicamento.getDthrFim(), 
						prescricaoMedicamento.getTipoFreqAprazamento().getSeq(), TipoItemAprazamento.SOLUCAO, 
						prescricaoMedicamento.getHoraInicioAdministracao(), prescricaoMedicamento.getIndSeNecessario(), 
						prescricaoMedicamento.getFrequencia()));
				dispensacaoFarmaciaVO
						.setMedicamento((this.getPrescricaoMedicaFacade()
								.obterDescricaoFormatadaMedicamentoSolucaoDispensacaoFarmacia(
										prescricaoMedicamento, aprazamento, false, true,
										true)));
				
				dispensacaoFarmaciaVO
				.setItensDispensacaoFarmacia(this
						.getPrescricaoMedicaFacade()
						.obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(
								prescricaoMedicamento, true));
			}
			else{
				String aprazamento = gerarAprazamentoString( this.getPrescricaoMedicaFacade().gerarAprazamento(prescricaoMedica.getId().getAtdSeq(), 
						prescricaoMedica.getId().getSeq(), prescricaoMedicamento.getDthrInicio(), prescricaoMedicamento.getDthrFim(), 
						prescricaoMedicamento.getTipoFreqAprazamento().getSeq(), TipoItemAprazamento.SOLUCAO, 
						prescricaoMedicamento.getHoraInicioAdministracao(), prescricaoMedicamento.getIndSeNecessario(), 
						prescricaoMedicamento.getFrequencia()));
				
				dispensacaoFarmaciaVO
				.setMedicamento((this.getPrescricaoMedicaFacade()
						.obterDescricaoAlteracaoMedicamentoSolucaoDispensacaoFarmacia(prescricaoMedicamento, aprazamento,true)));
				
				dispensacaoFarmaciaVO
				.setItensDispensacaoFarmacia(this
						.getPrescricaoMedicaFacade()
						.obterDescricaoDosagemAlteracaoItensMedicamentoSolucaoDispensacaoFarmacia(prescricaoMedicamento, true));
			}			
			
			if(prescricaoMedica.getDthrInicio()!=null){
				dispensacaoFarmaciaVO.setDthrInicio(sdf.format(prescricaoMedica.getDthrInicio()));	
			} else {
				dispensacaoFarmaciaVO.setDthrInicio("");
			}
			
			if(prescricaoMedica.getDthrFim()!=null){
				dispensacaoFarmaciaVO.setDthrFim(sdf.format(prescricaoMedica.getDthrFim()));	
			} else {
				dispensacaoFarmaciaVO.setDthrFim("");
			}
			
			if(prescricaoMedica.getCriadoEm()!=null){
				dispensacaoFarmaciaVO.setCriadoEm(sdf.format(prescricaoMedica.getCriadoEm()));
			} else {
				dispensacaoFarmaciaVO.setCriadoEm("");
			}
			if (prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getLeito() != null
					&& prescricaoMedica.getAtendimento().getLeito().getLeitoID() != null) {
				dispensacaoFarmaciaVO.setLocal("Leito:");
				
				validaPopulaLeitoID(dispensacaoFarmaciaVO, prescricaoMedica);
				
			} else if (prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getQuarto() != null
					&& prescricaoMedica.getAtendimento().getQuarto().getDescricao() != null) {
				dispensacaoFarmaciaVO.setLocal("Leito:");
				
				validaPopulaLeitoIdQuarto(dispensacaoFarmaciaVO, prescricaoMedica);
				
			} else if (prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getUnidadeFuncional() != null
					&& prescricaoMedica.getAtendimento().getUnidadeFuncional().getSeq() != null) {
				dispensacaoFarmaciaVO.setLocal("Unidade:");
				
				validaPopulaLeitoIdUnf(dispensacaoFarmaciaVO, prescricaoMedica);
				
			} else {
				dispensacaoFarmaciaVO.setLocal("");
				dispensacaoFarmaciaVO.setLocalID("");
			}
			if (prescricaoMedica != null && prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getPaciente() != null
					&& prescricaoMedica.getAtendimento().getPaciente().getProntuario() != null
					&& prescricaoMedica != null && prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getPaciente() != null
					&& prescricaoMedica.getAtendimento().getPaciente().getNome() != null) {
				dispensacaoFarmaciaVO.setProntuario(prescricaoMedica.getAtendimento().getPaciente()
						.getProntuario().toString().concat(_HIFEN_).concat(prescricaoMedica.getAtendimento().getPaciente().getNome()));
			} else {
				dispensacaoFarmaciaVO.setProntuario("");
			}
			if (prescricaoMedica != null && prescricaoMedica.getId() != null
					&& prescricaoMedica.getId().getSeq() != null) {
				dispensacaoFarmaciaVO.setPrescricaoMedicaSeq(prescricaoMedica.getId().getSeq()
						.toString());
			} else {
				dispensacaoFarmaciaVO.setPrescricaoMedicaSeq("");
			}
			if (prescricaoMedica != null
					&& prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getEspecialidade() != null
					&& prescricaoMedica.getAtendimento().getEspecialidade().getNomeEspecialidade() != null) {
				dispensacaoFarmaciaVO.setEspecialidade(prescricaoMedica.getAtendimento()
						.getEspecialidade().getNomeEspecialidade());
			} else {
				dispensacaoFarmaciaVO.setEspecialidade("");
			}
			if (prescricaoMedica != null
					&& prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getServidor() != null
					&& prescricaoMedica.getAtendimento().getServidor().getPessoaFisica() != null
					&& prescricaoMedica.getAtendimento().getServidor().getPessoaFisica().getNome() != null) {
				dispensacaoFarmaciaVO.setEquipe(prescricaoMedica.getAtendimento().getServidor()
						.getPessoaFisica().getNome());
			} else if (prescricaoMedica != null
					&& prescricaoMedica.getAtendimento() != null
					&& prescricaoMedica.getAtendimento().getServidor() != null
					&& prescricaoMedica.getAtendimento().getServidor().getPessoaFisica() != null
					&& prescricaoMedica.getAtendimento().getServidor().getPessoaFisica()
							.getNomeUsual() != null) {
				dispensacaoFarmaciaVO.setEquipe(prescricaoMedica.getAtendimento().getServidor()
						.getPessoaFisica().getNomeUsual());
			}  else {
				dispensacaoFarmaciaVO.setEquipe("");
			}
			if (prescricaoMedica.getServidor() != null
					&& prescricaoMedica.getServidor().getId() != null
					&& prescricaoMedica.getServidor().getId().getVinCodigo() != null) {
				dispensacaoFarmaciaVO.setSerVinCodigo(prescricaoMedica.getServidor().getId()
						.getVinCodigo().toString());
			} else {
				dispensacaoFarmaciaVO.setSerVinCodigo("");
			}
			listaMedicamentosVO.add(dispensacaoFarmaciaVO);
		}
		return listaMedicamentosVO;
	}

	/**
	 * @param dispensacaoFarmaciaVO
	 * @param prescricaoMedica
	 */
	public void validaPopulaLeitoIdUnf(
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO,
			MpmPrescricaoMedica prescricaoMedica) {
		if (prescricaoMedica.getAtendimento().getLeito() != null) {
			dispensacaoFarmaciaVO.setLocalID(prescricaoMedica.getAtendimento().getUnidadeFuncional().getSeq().toString().concat(_HIFEN_).
					concat(prescricaoMedica.getAtendimento().getLeito().getUnidadeFuncional().getDescricao()));
		}else {
			dispensacaoFarmaciaVO.setLocalID(prescricaoMedica.getAtendimento().getUnidadeFuncional().getSeq().toString().concat(_HIFEN_)
					.concat(prescricaoMedica.getAtendimento().getUnidadeFuncional().getDescricao()));
		}
	}

	/**
	 * @param dispensacaoFarmaciaVO
	 * @param prescricaoMedica
	 */
	public void validaPopulaLeitoID(
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO,
			MpmPrescricaoMedica prescricaoMedica) {
		if (prescricaoMedica.getAtendimento().getLeito() != null) {
				dispensacaoFarmaciaVO.setLocalID(prescricaoMedica.getAtendimento().getLeito().getLeitoID().toString().concat(_HIFEN_).
				concat(prescricaoMedica.getAtendimento().getLeito().getUnidadeFuncional().getDescricao()));
		} else {
			dispensacaoFarmaciaVO.setLocalID(prescricaoMedica.getAtendimento().getLeito().getLeitoID().toString());
		}
	}

	/**
	 * @param dispensacaoFarmaciaVO
	 * @param prescricaoMedica
	 */
	public void validaPopulaLeitoIdQuarto(
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO,
			MpmPrescricaoMedica prescricaoMedica) {
		if (prescricaoMedica.getAtendimento().getLeito() != null) {
			dispensacaoFarmaciaVO.setLocalID(prescricaoMedica.getAtendimento().getQuarto().getDescricao().concat(_HIFEN_).
				concat(prescricaoMedica.getAtendimento().getLeito().getUnidadeFuncional().getDescricao()));
		} else {
			dispensacaoFarmaciaVO.setLocalID(prescricaoMedica.getAtendimento().getQuarto().getDescricao());
		}
	}

	/**
	 * @param dispensacaoFarmaciaVO
	 * @param buscaConselhoProfissionalServidorVO
	 */
	private void populaDispensacaoFarmaciaVONomeConselhoProfissionalServidor(
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO,
			BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO) {
		if(buscaConselhoProfissionalServidorVO.getNome()!=null){
			dispensacaoFarmaciaVO.setSolicitante(buscaConselhoProfissionalServidorVO.getNome());	
		} else {
			dispensacaoFarmaciaVO.setSolicitante("");
		}
	}

	/**
	 * @param dispensacaoFarmaciaVO
	 * @param buscaConselhoProfissionalServidorVO
	 */
	private void populaDispensacaoVOSiglaConselho(
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO,
			BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO) {
		if(buscaConselhoProfissionalServidorVO.getSiglaConselho()!=null){
			dispensacaoFarmaciaVO.setSigla(buscaConselhoProfissionalServidorVO
					.getSiglaConselho());
		} else {
			dispensacaoFarmaciaVO.setSigla("");
		}
	}

	/**
	 * @param dispensacaoFarmaciaVO
	 * @param buscaConselhoProfissionalServidorVO
	 */
	private void populaDispensacaoVONumeroRegistroConselho(
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO,
			BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO) {
		if(buscaConselhoProfissionalServidorVO.getNumeroRegistroConselho()!=null){
			dispensacaoFarmaciaVO.setNroRegistro(buscaConselhoProfissionalServidorVO
					.getNumeroRegistroConselho());
		} else {
			dispensacaoFarmaciaVO.setNroRegistro("");
		}
	}
	
	private String gerarAprazamentoString(List<String> gerarAprazamento) {
		StringBuilder aprazamento = new StringBuilder("");
		if (gerarAprazamento != null) {
			for (String string : gerarAprazamento) {
				aprazamento.append(string);
				aprazamento.append("          ");
			}
		}
		return aprazamento.toString();
	}
	

	public List<MpmPrescricaoMdto> ordenarMedicamentos(List<MpmPrescricaoMdto> lista) {
		class OrdenacaoMedicamentos implements Comparator<MpmPrescricaoMdto> {
			@Override
			public int compare(MpmPrescricaoMdto o1, MpmPrescricaoMdto o2) {
				
				if (o1.getIndAntiMicrobiano() == true && o2.getIndAntiMicrobiano() == false) {
					return -1;
				} 
				if (o2.getIndAntiMicrobiano() == true && o1.getIndAntiMicrobiano() == false) {
					return 1;
				} else {
						return o1.getDescricaoFormatada().compareTo(o2.getDescricaoFormatada());
				}
			}
		}
		List<MpmPrescricaoMdto> listaMedicamentos = new ArrayList<MpmPrescricaoMdto>();
		if (lista != null){
			listaMedicamentos.addAll(lista);			
		}
		Collections.sort(listaMedicamentos, new OrdenacaoMedicamentos());
		return listaMedicamentos;
	}
	
	public List<DispensacaoFarmaciaVO> ordenarDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> lista) {
		class OrdenacaoDispensacaoFarmacia implements Comparator<DispensacaoFarmaciaVO> {
			@Override
			public int compare(DispensacaoFarmaciaVO o1, DispensacaoFarmaciaVO o2) {
						return o1.getFarmacia().compareTo(o2.getFarmacia());
			}
		}
		List<DispensacaoFarmaciaVO> listaMedicamentos = new ArrayList<DispensacaoFarmaciaVO>();
		if (lista != null){
			listaMedicamentos.addAll(lista);			
		}
		Collections.sort(listaMedicamentos, new OrdenacaoDispensacaoFarmacia());
		return listaMedicamentos;
	}
	
	/**
	 * @param atdSeq
	 * @param dthrInicio
	 * @param dthrFim
	 * @param unfSeq
	 * @param dthrMovimento
	 * @throws ApplicationBusinessException 
	 * */
	public Boolean verificarFarmaciaMovimentacaoMedicamento(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento) throws ApplicationBusinessException{
		boolean isPrescricaoMedicamento = verificarPrescricaoMedicamento(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
		boolean isPrescricaoItemMedicamento = verificarPrescricaoItemMedicamento(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);

		if(!isPrescricaoMedicamento){
			if(!isPrescricaoItemMedicamento){
				return Boolean.FALSE;
			}else{
				return Boolean.TRUE;
			}
		}else{
			return Boolean.TRUE;
		}
	}
	
	// C_PMD
	public boolean verificarPrescricaoItemMedicamento(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento) throws ApplicationBusinessException {
		List<LocalDispensa2VO> listaPrescricaoItemMedicamento = getPrescricaoMedicaFacade().listarPrescricaoItemMedicamentoFarmaciaMov(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
		List<LocalDispensa2VO> removerListaPrescricaoItemMedicamento = new ArrayList<LocalDispensa2VO>();
		
		for (LocalDispensa2VO prescricaoItemMedicamento : listaPrescricaoItemMedicamento) {
			Short localDispensa2 = this.getDispensaMedicamentoRN().getLocalDispensa2(
					prescricaoItemMedicamento.getAtendimentoSeq(), 
					prescricaoItemMedicamento.getMedMatCodigo(), 
					prescricaoItemMedicamento.getDose(),
					prescricaoItemMedicamento.getFdsSeq(),
					null);
			
			if(unfSeq != localDispensa2){
				removerListaPrescricaoItemMedicamento.add(prescricaoItemMedicamento);
			}
			
			if(!removerListaPrescricaoItemMedicamento.isEmpty()){
				listaPrescricaoItemMedicamento.removeAll(removerListaPrescricaoItemMedicamento);
			}
		}
		return (listaPrescricaoItemMedicamento != null & !listaPrescricaoItemMedicamento.isEmpty() ? true : false);
	}	
	
	// C_VMP
	public boolean verificarPrescricaoMedicamento(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento) throws ApplicationBusinessException {
		List<LocalDispensa2VO> listaPrescricaoMedicamento = getPrescricaoMedicaFacade().listarPrescricaoMedicamentoFarmaciaMov(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
		List<LocalDispensa2VO> removerListaPrescricaoMedicamento = new ArrayList<LocalDispensa2VO>();
		
		for (LocalDispensa2VO prescricaoMedicamento : listaPrescricaoMedicamento) {
			Short localDispensa2 = this.getDispensaMedicamentoRN().getLocalDispensa2(
					prescricaoMedicamento.getAtendimentoSeq(), 
					prescricaoMedicamento.getMedMatCodigo(), 
					prescricaoMedicamento.getDose(),
					prescricaoMedicamento.getFdsSeq(),
					null);
			
			if(unfSeq != localDispensa2){
				removerListaPrescricaoMedicamento.add(prescricaoMedicamento);
			}
			
			if(!removerListaPrescricaoMedicamento.isEmpty()){
				listaPrescricaoMedicamento.removeAll(removerListaPrescricaoMedicamento);
			}
		}
		return (listaPrescricaoMedicamento != null & !listaPrescricaoMedicamento.isEmpty() ? true : false);
	}
	
	/**
	 * @ORADB - IMPRIME_MOV_MED_FARMACIA_DISP
	 * 
	 *  
	 */
	public DispensacaoFarmaciaVO imprimirMovimentacaoMededicamentoFarmaciaDispensa(
			Date dthrMovimento, 
			DispensacaoFarmaciaVO dispensacaoFarmaciaVO, List<Short> unidadeFuncionaisFarmaciaMe, Short farmPadrao) throws BaseException {
		ArrayList<AghUnidadesFuncionais> listaUnidadesFuncional = (ArrayList<AghUnidadesFuncionais>) getAghuFacade().listarUnidadeFarmacia();
		
		if (listaUnidadesFuncional.isEmpty()){
			throw new ApplicationBusinessException(
					DispensaMedicamentoONExceptionCode.UNIDADE_FARMACIA_NAO_CADASTRADA);
		}
		ArrayList<String> farmacias = new ArrayList<String>();

		// Old: Caminho de rede -> ImpImpressora.
		// ArrayList<String> impressoras = new ArrayList<String>();
		Set<ImpImpressora> impressorasCups = new HashSet<ImpImpressora>();
		for(AghUnidadesFuncionais unidadeFuncional: listaUnidadesFuncional){
			Short unidadeFuncionalSeq = unidadeFuncional.getSeq();
			Byte nroViasPme;
			if (unidadeFuncional.getNroViasPme() == null) {
				nroViasPme = Byte.valueOf("2");
			} else {
				nroViasPme = unidadeFuncional.getNroViasPme();
			}
			
			if(this.verificarFarmaciaMe(unidadeFuncionaisFarmaciaMe, unidadeFuncionalSeq)) {

				ImpImpressora impressora = this.relatorioDispensacaoFarmacia(unidadeFuncionalSeq, nroViasPme,
						farmPadrao == unidadeFuncionalSeq, dispensacaoFarmaciaVO.getPrescricaoMdto());
				
				if (impressora != null) {
					farmacias.add(unidadeFuncionalSeq.toString());
					impressorasCups.add(impressora);
				}
			}
		}
	
		dispensacaoFarmaciaVO.setFarmacias(farmacias);
		dispensacaoFarmaciaVO.setImpressorasCups(impressorasCups);
		return dispensacaoFarmaciaVO;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected DispensaMedicamentoRN getDispensaMedicamentoRN() {
		return dispensaMedicamentoRN;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
}
