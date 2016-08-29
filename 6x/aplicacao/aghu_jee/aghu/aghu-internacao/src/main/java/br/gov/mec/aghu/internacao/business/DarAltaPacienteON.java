package br.gov.mec.aghu.internacao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDadosAltaPaciente;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDatasVO;
import br.gov.mec.aghu.internacao.business.vo.FlagsValidacaoDadosAltaPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.internacao.vo.VerificaPermissaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity" , "PMD.AtributoEmSeamContextManager"})
@Stateless
public class DarAltaPacienteON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(DarAltaPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2956581850353757791L;
		
	public enum DarAltaPacienteCode {
		PEDE_CONFIRMACAO_TIPO_ALTA, MENSAGEM_CONFIRMADA_PERMISSAO, MENSAGEM_CONFIRMACAO_ALTA, MENSAGEM_CONFIRMACAO_SAIDA, MENSAGEM_CONFIRMACAO_ALTA_SAIDA, MENSAGEM_CONFIRMACAO_ALTERACAO_ALTA, MENSAGEM_LCTOS_FAT;
	}
	
	private enum DarAltaPacienteExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_DADOS_ALTA_PACIENTE, ERRO_NEGOCIO_ORA, AIN_00151, AIN_00432, AIN_00147, AIN_00146, AIN_00852, AIN_00394, AIN_DOC_OBITO,
		AIN_00145, AIN_00132, AIN_00736, AIN_00421, AIN_00505, AIN_00334, AIN_00890, AIN_00395, ERRO_DT_SAIDA_PAC, AIN_00396, AIN_00391,
		AEL_01094, MENSAGEM_LCTOS_FAT;
	}
	
	
	/**
	 * Método resposável por buscar Instituicoes Hospitalares, conforme a string passada como parametro,
	 * que é comparada com o codigo e a nome da Instituicao Hospitalar
	 * É utilizado pelo converter AghInstituicoesHospitalaresConverter.
	 * @param nome ou codigo
	 * @return Lista de AghInstituicoesHospitalares
	 */	
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(final Object objPesquisa) {
		return getAghuFacade().pesquisarInstituicaoHospitalarPorCodigoENome(objPesquisa);
	}
	
	/**
	 * traducao da function ainc_alta_medica
	 * @param internacaoSeq
	 * @return
	 */
	public DadosAltaSumarioVO buscarDadosAltaMedica(final Integer internacaoSeq) throws ApplicationBusinessException{
		DadosAltaSumarioVO dadosAlta = null;
		dadosAlta = this.buscarDadosAltaSumario(internacaoSeq);
		if(dadosAlta == null){
			dadosAlta = this.consultarDataAltaDeSumarioAlta(internacaoSeq);
		}
		return dadosAlta;
	}
	
	private DadosAltaSumarioVO consultarDataAltaDeSumarioAlta(final Integer internacaoSeq){
		DadosAltaSumarioVO dados = null;
		final Date data = getPrescricaoMedicaFacade().pesquisarDataAltaInternacao(internacaoSeq);
		if(data != null){
			dados = new DadosAltaSumarioVO();
			dados.setDthrAltaAsu(data);
		}
		return dados;
	}
	
	private DadosAltaSumarioVO buscarDadosAltaSumario(final Integer internacaoSeq){
		return getPrescricaoMedicaFacade().buscarDadosAltaSumario(internacaoSeq);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void validarDadosInformados(final AinInternacao internacao)throws ApplicationBusinessException{
		final AghParametros p = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_O);
		final String tamO = p.getVlrTexto();
		
		final AghParametros pDocObito = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_NAO_DOC_OBITO);
		final short conv = pDocObito.getVlrNumerico().shortValue();
		
		if(internacao.getDthrAltaMedica() != null && internacao.getDthrAltaMedica().after(new Date())){
			//AIN-00394 - A data de alta médica não deve ser posterior a data corrente.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00394);
		}
		if(internacao.getTipoAltaMedica() != null && !internacao.getTipoAltaMedica().getCodigo().equalsIgnoreCase(tamO) && internacao.getDocObito() != null){
			//AIN-00852 - Não informar número de atestado de óbito quando o tipo de alta médica não for óbito.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00852);
		}
		if(internacao.getTipoAltaMedica() != null && internacao.getTipoAltaMedica().getCodigo().equalsIgnoreCase(tamO) && internacao.getConvenioSaude() != null && internacao.getConvenioSaude().getCodigo().shortValue() != conv &&  internacao.getDocObito() == null){
			//AIN-00851 - Informe o número da declaração de óbito.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_DOC_OBITO);
		}
		if(internacao.getDtSaidaPaciente() != null && internacao.getDtSaidaPaciente().after(new Date())){
			//AIN-00395 - A data de saída do paciente não deve ser posterior a data corrente.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00395);
		}
		
		if(internacao.getDthrAltaMedica()!= null && internacao.getDtSaidaPaciente() != null && internacao.getDthrAltaMedica().after(internacao.getDtSaidaPaciente())){
			//AIN-00151 - A data saída do paciente deve ser maior ou igual a data da alta.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00151);
		}
		if((internacao.getDthrAltaMedica()!= null && internacao.getTipoAltaMedica() == null) ||(internacao.getDthrAltaMedica()== null && internacao.getTipoAltaMedica() != null) ){
			//AIN-00432 - A data de alta médica e o tipo de alta médica devem ser informados ou não.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00432);
		}
		if(internacao.getDthrInternacao()!= null && internacao.getDtSaidaPaciente() != null && internacao.getDtSaidaPaciente().before(internacao.getDthrInternacao())){
			//AIN-00147 - A data da saída do paciente deve ser maior ou igual a data da internação.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00147);
		}
		if(internacao.getDthrInternacao()!= null && internacao.getDtPrevAlta() != null && DateUtil.validaDataTruncadaMaior( internacao.getDthrInternacao(), internacao.getDtPrevAlta())){
			//AIN-00146 - A data de previsão de alta deve ser maior ou igual a data da internação.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00146);
		}
		if(internacao.getDthrInternacao()!= null && internacao.getDthrAltaMedica() != null && internacao.getDthrAltaMedica().before(internacao.getDthrInternacao())){
			//AIN-00145 - A data da alta médica deve ser maior ou igual a data da internação.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00145);
		}
		if((internacao.getLeito() != null && internacao.getQuarto() != null) || (internacao.getLeito() != null && internacao.getUnidadesFuncionais() != null) || (internacao.getQuarto() != null && internacao.getUnidadesFuncionais() != null) ){
			//AIN-00132 - Apenas um entre Leito, Unidade Funcional, Quarto pode ser especificado.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00132);
		}
		if(internacao.getDthrUltimoEvento() != null && internacao.getDthrInternacao() != null && internacao.getDthrUltimoEvento().before(internacao.getDthrInternacao())){
			//AIN-00736 - O último evento deve ser maior ou igual a data da internação
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00736);
		}
		if(!((internacao.getInstituicaoHospitalarTransferencia() == null) || (internacao.getInstituicaoHospitalarTransferencia() != null && internacao.getTipoAltaMedica() != null && internacao.getDthrAltaMedica() != null))){
			//AIN-00421 - Instituição Hospitalar de transferência não pode ser informada sem preencher dados da alta
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00421);
		}
		if(internacao.getDthrUltimoEvento() != null && internacao.getDthrAltaMedica() != null && internacao.getDthrAltaMedica().before(internacao.getDthrUltimoEvento())){
			//AIN-00505 - Data de alta médica não pode ser menor que a data do último evento do paciente.
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00505);
		}
		//### Validacao nao implementada ###
		//AIN-00284
		//AIN_INT_CK12 ((IHO_SEQ_ORIGEM IS NULL AND ATU_SEQ IS NOT NULL) OR (ATU_SEQ IS NULL AND IHO_SEQ_ORIGEM IS NOT NULL) OR (IHO_SEQ_ORIGEM IS NULL AND ATU_SEQ IS NULL)       )
	}
	

	public void validarDadosDeInformadosComDadosDoBancoEstorno(final AinInternacao internacao, final Boolean dthrAltaMedicaBD , final Boolean dtSaidaPacBD, final Boolean tipoAltaMedicaBD)throws ApplicationBusinessException{
		if(dthrAltaMedicaBD != null && dtSaidaPacBD != null && tipoAltaMedicaBD != null){
			//EVT_PRE_UPDATE
			if((dthrAltaMedicaBD && dtSaidaPacBD && tipoAltaMedicaBD) && !((internacao.getDtSaidaPaciente() == null && internacao.getDthrAltaMedica() == null && internacao.getTipoAltaMedica() == null) || (internacao.getDtSaidaPaciente() != null && internacao.getDthrAltaMedica() != null && internacao.getTipoAltaMedica() != null))){
				//AIN-00396 - Tipo de alta, data/hora da alta e saida do pacientes após informados só podem ser estornados através da opção 'Estornar Alta'
				throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00396);
			}
			//FIM EVT_PRE_UPDATE
			
			if(internacao.getDtSaidaPaciente() == null && dtSaidaPacBD == false){
				//ERRO_DT_SAIDA_PAC - Temporariamente não é permitido alta sem saída do paciente.
				throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.ERRO_DT_SAIDA_PAC);
			}
		}
	}
//	public DarAltaPacienteCode validaDadosDeInformadosComDadosDoBancoEstorno(AinInternacao internacao, Boolean dthrAltaMedicaBD , Boolean dtSaidaPacBD, Boolean tipoAltaMedicaBD){
//		DarAltaPacienteCode exCode = null;
//		if(dthrAltaMedicaBD != null && dtSaidaPacBD != null && tipoAltaMedicaBD != null){
//			//EVT_PRE_UPDATE
//			if((dthrAltaMedicaBD && dtSaidaPacBD && tipoAltaMedicaBD) && !((internacao.getDtSaidaPaciente() == null && internacao.getDthrAltaMedica() == null && internacao.getTipoAltaMedica() == null) || (internacao.getDtSaidaPaciente() != null && internacao.getDthrAltaMedica() != null && internacao.getTipoAltaMedica() != null))){
//				//AIN-00396 - Tipo de alta, data/hora da alta e saida do pacientes após informados só podem ser estornados através da opção 'Estornar Alta'
//				exCode = DarAltaPacienteCode.AIN_00396;
//			}
//			//FIM EVT_PRE_UPDATE
//		}
//		return exCode;
//	}
	
	public DarAltaPacienteCode validarDadosDeInformadosComDadosDoBanco(final AinInternacao internacao, final Boolean dthrAltaMedicaBD , final Boolean dtSaidaPacBD, final Boolean tipoAltaMedicaBD){
		DarAltaPacienteCode exCode = null;
		if(dthrAltaMedicaBD != null && dtSaidaPacBD != null && tipoAltaMedicaBD != null){
			
			//AINC_CONFIRMACAO
			if((dthrAltaMedicaBD == false && internacao.getDthrAltaMedica() != null && dtSaidaPacBD == false && internacao.getDtSaidaPaciente() == null )){
				//Confirma Alta do Paciente?
				exCode = DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTA;
				//this.message = getResourceBundleValue(DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTA.toString());
				
			}else if(dthrAltaMedicaBD && internacao.getDthrAltaMedica() != null && dtSaidaPacBD == false && internacao.getDtSaidaPaciente() != null){
				//Confirma Saida do Paciente?
				exCode = DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_SAIDA;
				//this.message = getResourceBundleValue(DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_SAIDA.toString());
			}else if(dthrAltaMedicaBD == false && internacao.getDthrAltaMedica() != null && dtSaidaPacBD == false && internacao.getDtSaidaPaciente() != null ){
				//Confirma Alta/Saida do Paciente?
				exCode = DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTA_SAIDA;
				//this.message = getResourceBundleValue(DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTA_SAIDA.toString());
			}else{
				//Confirma Alteração dos Dados da Alta?
				exCode = DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTERACAO_ALTA;
				//this.message = getResourceBundleValue(DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTERACAO_ALTA.toString());
			}
			//FIM AINC_CONFIRMACAO
		}
		return exCode;
	}
	
	//### verifica_lctos_fat; (procedure do modulo de faturamento) ### FatkCthRN.rnCthcVerDatas
	public DarAltaPacienteCode validarLancamentosFaturamento(final AinInternacao internacao, final Date dataFimVinculoServidor) throws BaseException {
		DarAltaPacienteCode exCode = null;
		final RnCthcVerDatasVO rnCthcVerDatasVO = getFaturamentoFacade().rnCthcVerDatas(internacao.getSeq(), internacao.getDthrAltaMedica(), null, "A", dataFimVinculoServidor);
		if(rnCthcVerDatasVO != null && !rnCthcVerDatasVO.getRetorno()){
			//final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			exCode = DarAltaPacienteCode.MENSAGEM_LCTOS_FAT;
			
			//this.message = sdf.format(rnCthcVerDatasVO.getDataLimite());
		}
		return exCode;
	}
	
	
	public DarAltaPacienteCode validarDadosDaAltaPaciente(final AinInternacao internacao) throws ApplicationBusinessException {
		DarAltaPacienteCode exCode = null;
		
		//### PRE_COMMIT
		final AghParametros p1 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO);
		final AghParametros p2 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H);
		final AghParametros p3 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_GENERICO);
		final AghParametros p4 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_UTIN);
		
		//### IMPLEMENTAR!! Foi implemantada no método verificaLancamentosFaturamento ###
		//### verifica_lctos_fat; (procedure do modulo de faturamento) ### FatkCthRN.rnCthcVerDatas
		
		Short unidadeFunc = null;
		DominioSimNao indCti = null;
		if(internacao.getTipoAltaMedica() != null && StringUtils.isNotBlank(internacao.getTipoAltaMedica().getCodigo())){
			final String tamCodigo = internacao.getTipoAltaMedica().getCodigo();
			if(!tamCodigo.equalsIgnoreCase(p1.getVlrTexto()) && !tamCodigo.equalsIgnoreCase(p2.getVlrTexto()) && !tamCodigo.equalsIgnoreCase(p3.getVlrTexto())){
				if(internacao.getLeito() != null){
					unidadeFunc = buscarUnidadeFuncionalSeqDoLeito(internacao.getLeito().getLeitoID()); //TODO
					indCti = getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unidadeFunc, ConstanteAghCaractUnidFuncionais.UNID_CTI);
				}else if(internacao.getQuarto()!= null){
					unidadeFunc = buscarUnidadeFuncionalSeqDoQuarto(internacao.getQuarto().getNumero());
					indCti = getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unidadeFunc, ConstanteAghCaractUnidFuncionais.UNID_CTI);
				}else{
					unidadeFunc = internacao.getUnidadesFuncionais() != null ? internacao.getUnidadesFuncionais().getSeq() : null;
					indCti = getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unidadeFunc, ConstanteAghCaractUnidFuncionais.UNID_CTI);
				}
				
				if(internacao.getConvenioSaude() != null && internacao.getConvenioSaude().getCodigo() == 1 && DominioSimNao.S.equals(indCti) && p4.getVlrNumerico() != null && p4.getVlrNumerico().shortValue() != unidadeFunc ){
					//### AINC_PEDE_CONFIRMACAO_TIPO_ALT ###
					exCode = DarAltaPacienteCode.PEDE_CONFIRMACAO_TIPO_ALTA;
					//this.message = getResourceBundleValue(DarAltaPacienteCode.PEDE_CONFIRMACAO_TIPO_ALTA.toString());
				}
			}else{
				//EVT_PRE_UPDATE
				final AghAtendimentos atendimento = this.obterAtendimentoDaInternacao(internacao.getSeq());
				if(atendimento == null){
					//AIN-00334 - Dados do cadastro do paciente não encontrados
					throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00334);
				}
			}
		}
		return exCode;
	}
	
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public DarAltaPacienteCode validarPermissoesDarAltaPaciente(final AinInternacao internacao, VerificaPermissaoVO voRetorno, String tipoAltaMedica){
		DarAltaPacienteCode exCode = null;
		this.verificarPermissao(internacao.getSeq(), internacao.getDthrAltaMedica(), voRetorno);
//		if(internacao.getConvenioSaude() != null && internacao.getConvenioSaude().getCodigo() == 1 && voRetorno.getDiasInt() != null && voRetorno.getDiasPerm() != null && voRetorno.getDiasInt() < (voRetorno.getDiasPerm()/2)){
		if(internacao.getConvenioSaude() != null && internacao.getConvenioSaude().getCodigo() == 1 && 
			voRetorno.getDiasInt() != null && voRetorno.getDiasPerm() != null && 
			voRetorno.getDiasInt() < (voRetorno.getDiasPerm().floatValue()/2) &&
			( !"E".equalsIgnoreCase(tipoAltaMedica) && !"O".equalsIgnoreCase(tipoAltaMedica)) ){
			//### AINC_PEDE_CONFIRMACAO_PERM(v_tempo,v_dias_perm) ###
			exCode = DarAltaPacienteCode.MENSAGEM_CONFIRMADA_PERMISSAO;
			//final String msg = getResourceBundleValue(DarAltaPacienteCode.PEDE_CONFIRMACAO_PERMISSAO.toString());
			//this.message = MessageFormat.format(msg, voRetorno.getDiasInt(), voRetorno.getDiasPerm());
		}
		return exCode;
	}
	
	public Short buscarUnidadeFuncionalSeqDoLeito(final String leitoID) {
		return getCadastrosBasicosInternacaoFacade().buscarSeqUnidadeFuncionalSeqDoLeito(leitoID);
	}

	public Short buscarUnidadeFuncionalSeqDoQuarto(final Short qrtNumero){
		return getCadastrosBasicosInternacaoFacade().buscarSeqUnidadeFuncionalSeqDoQuarto(qrtNumero);
	}
	
	/**
	 * ORADB AINP_VERIFICA_PERM
	 */
	public void verificarPermissao(final Integer intSeq, final Date intDthrAltaMedica, final VerificaPermissaoVO vo){
		Date dataInternacao  = null;
		Date dataAlta = null;
		Date dataSup = null;
		AinInternacao item = null;
		if(intSeq != null){
			item = ainInternacaoDAO.obterPorChavePrimaria(intSeq);
		}
		if(item != null){
			final Calendar calAux = Calendar.getInstance();
			if(item.getDthrInternacao() != null){
				calAux.setTime(item.getDthrInternacao());
				calAux.set(Calendar.HOUR, 0);
				calAux.set(Calendar.MINUTE, 0);
				calAux.set(Calendar.SECOND, 0);
				calAux.set(Calendar.MILLISECOND, 0);
				dataInternacao = new Date();
				dataInternacao.setTime(calAux.getTime().getTime()) ;
			}
			if(item.getDthrAltaMedica() != null){
				calAux.setTime(item.getDthrAltaMedica());
				calAux.set(Calendar.HOUR, 0);
				calAux.set(Calendar.MINUTE, 0);
				calAux.set(Calendar.SECOND, 0);
				calAux.set(Calendar.MILLISECOND, 0);
				dataAlta = new Date();
				dataAlta.setTime(calAux.getTime().getTime());
			}
			if(item.getItemProcedimentoHospitalar()!= null){
				vo.setDiasPerm(item.getItemProcedimentoHospitalar().getQuantDiasFaturamento());
			}
		}
		
		if(dataAlta == null || (intDthrAltaMedica != null && intDthrAltaMedica.before(dataAlta))){
			dataSup = intDthrAltaMedica;
		}else {
			dataSup = dataAlta;
		}
		if(dataInternacao != null && dataSup != null){
			final Calendar cDataSup = Calendar.getInstance();
			cDataSup.setTime(dataSup);
			final Calendar cDataInternacao = Calendar.getInstance();
			cDataInternacao.setTime(dataInternacao);
		
			//Diferenca entre as datas em dias. (24*60*60*1000) eh a quantidade de milisegundos em um dia.
			final Long diasIntLong = (cDataSup.getTimeInMillis() - cDataInternacao.getTimeInMillis())/(24*60*60*1000);
			vo.setDiasInt(diasIntLong.intValue());
		}
		if(vo.getDiasPerm() != null && vo.getDiasInt() != null && vo.getDiasInt() > (vo.getDiasPerm() * 2)){
			vo.setPermMaior(DominioSimNao.S);
		}else{
			vo.setPermMaior(DominioSimNao.N);
		}
	}
	
	@Secure("#{s:hasPermission('alta','alterar')}")
	public String alterarTipoAltaObito(final Integer pacCodigo, final Integer intSeq, final Date dtSaidaPaciente, final Date dthrAltaMedica) throws ApplicationBusinessException{
		String codigoTipoAltaMedica = null;
		try{
			//se não existir o parametro na base, ou ele for != 1, não executa a validaçao do óbito
			final AghParametros p1 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_TRADUZ_OBITO);
			if(p1 != null && p1.getVlrNumerico()!= null && p1.getVlrNumerico().intValue() == 1){
				final AipPacientes pac =  getPacienteFacade().obterPacientePorCodigo(pacCodigo);
				if(pac == null){
					//AIN-00334 - Dados do cadastro do paciente não encontrados
					throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00334);
				}
				final AghAtendimentos atendimento = this.obterAtendimentoDaInternacao(intSeq);
				if(atendimento == null){
					//AIN-00334 - Dados do cadastro do paciente não encontrados
					throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00334);
				}
				final Calendar auxCal = Calendar.getInstance();
				auxCal.setTime(dthrAltaMedica);
				//auxCal.add(Calendar.DAY_OF_MONTH, 1);
				final Calendar auxCal2 = Calendar.getInstance();
				auxCal2.setTime(atendimento.getDthrInicio());
	
				if(((auxCal.getTimeInMillis() - auxCal2.getTimeInMillis())/(24*60*60*1000)) < 1){
//				if(auxCal.getTime().before(dthrAltaMedica)){
					codigoTipoAltaMedica = "C";
				}else{
					codigoTipoAltaMedica = "D";
				}				
			}
		}catch (final ApplicationBusinessException e) {
			if(!e.getCode().equals(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE)){
				throw e;
			}
		}
		return codigoTipoAltaMedica;
	}
	
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AghAtendimentos obterAtendimentoDaInternacao(final Integer internacaoSeq) {
		AghAtendimentos atendimento = null;
		if (internacaoSeq != null) {
			atendimento = getAghuFacade().buscarAtendimentosPorCodigoInternacao(internacaoSeq);
		}
		return atendimento;
	}

	/*public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}*/
	
	
	/**
	 * Método responsável pela atualizacao de uma internação.
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	
	@Secure("#{s:hasPermission('internacao','alterar')}")
	public AinInternacao atualizarInternacao(AinInternacao internacao, String nomeMicrocomputador) throws BaseException {
		try{					
			final AinInternacao internacaoOld = this.internacaoFacade.obterInternacaoAnterior(internacao.getSeq());
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			internacao = this.getAinInternacaoDAO().merge(internacao);
			//CHAMADA DE TRIGGERS DE ATUALIZAÇÃO
			this.getInternacaoFacade().atualizarInternacao(internacao, internacaoOld, nomeMicrocomputador,servidorLogado, new Date(), false);

			this.getAinInternacaoDAO().flush();			
		}
		catch(final BaseException e){
			logError("Exceção BaseException capturada, lançada para cima." + e.getMessage(), e);
			throw e;
		}
		catch(final BaseRuntimeException e){
			logError("Erro ao gravar dados da Alta do Paciente. " + e.getMessage(), e);
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.ERRO_PERSISTIR_DADOS_ALTA_PACIENTE);
		}
		catch (final RuntimeException e) {
			tratarException(e, null);
		}
		catch(final Exception e){
			logError("Erro ao gravar dados da Alta do Paciente. " + e.getMessage(), e);
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.ERRO_PERSISTIR_DADOS_ALTA_PACIENTE);
		}
		return internacao;
	}
	
	private void tratarException(final Throwable e, final Throwable eAnterior) throws ApplicationBusinessException {
		final String msg = e.getMessage();
		if (msg != null && !msg.isEmpty() && msg.indexOf("AIN-00391") >= 0) {
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AIN_00391);
		} else if (msg != null && !msg.isEmpty() && msg.indexOf("AEL-01094") >= 0) {
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.AEL_01094);
		} else {
			final Throwable en = e.getCause();
			if (en != null && (eAnterior == null || !en.equals(eAnterior))) {
				tratarException(en, e);
			}
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.ERRO_NEGOCIO_ORA, msg);
		}
	}	
	
	// ### Metodos de PESQUISA para o "Dar Alta ao Paciente"
	public Long pesquisaInternacoesParaDarAltaPacienteCount(final Integer prontuario) {
		return getAinInternacaoDAO().pesquisaInternacoesPorProntuarioCount(prontuario);
	}

	//@Secure
	public List<AinInternacao> pesquisaInternacoesParaDarAltaPaciente(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Integer prontuario) {
		return getAinInternacaoDAO().pesquisaInternacoesPorProntuario(firstResult, maxResult, orderProperty, asc, prontuario);
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	public boolean gravarDataAltaPaciente(final FlagsValidacaoDadosAltaPacienteVO flagsValidacaoDadosAltaPacienteVO, VerificaPermissaoVO verificaPermissaoVO,
			final DominioTipoDadosAltaPaciente tipoDadoAltaPaciente, final AinInternacao internacao,  final AinTiposAltaMedica tipoAltaMedica,
			final AghInstituicoesHospitalares instituicaoHospitalar, final Date dthrAltaMedica, final Date dtSaidaPaciente, final Integer docObito,
			final boolean dtSaidaPacBD, final boolean dthrAltaMedicaBD, final boolean tipoAltaMedicaBD, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		DarAltaPacienteCode exCode = null;

		if (flagsValidacaoDadosAltaPacienteVO.isValidaDados()) {
			if (tipoDadoAltaPaciente.equals(DominioTipoDadosAltaPaciente.M)) {
				internacao.setIndAltaManual(DominioSimNao.S);
			} else {
				internacao.setIndAltaManual(DominioSimNao.N);
			}
			internacao.setTipoAltaMedica(tipoAltaMedica);
			internacao.setInstituicaoHospitalarTransferencia(instituicaoHospitalar);
			dthrAltaMedica.setSeconds(0); //Remove qualquer diferença em segundos para futuras comparações
			internacao.setDthrAltaMedica(dthrAltaMedica);
			internacao.setDtSaidaPaciente(dtSaidaPaciente);
			internacao.setDocObito(docObito);

			this.validarDadosInformados(internacao);
			flagsValidacaoDadosAltaPacienteVO.setValidaDados(false);
		}
		if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosInformadosEstorno()) {
			this.validarDadosDeInformadosComDadosDoBancoEstorno(internacao, dthrAltaMedicaBD, dtSaidaPacBD, tipoAltaMedicaBD);
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformadosEstorno(false);
		}

		if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosFaturamento() && exCode == null) {
			exCode = this.validarLancamentosFaturamento(internacao,dataFimVinculoServidor);
		}

		if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosDaAltaPaciente() && exCode == null) {
			exCode = this.validarDadosDaAltaPaciente(internacao);
		}
		if (flagsValidacaoDadosAltaPacienteVO.isValidaPermissoesDarAltaPaciente() && exCode == null) {
			exCode = this.validarPermissoesDarAltaPaciente(internacao, verificaPermissaoVO, tipoAltaMedica.getCodigo());
		}
		if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosInformados() && exCode == null) {
			exCode = this.validarDadosDeInformadosComDadosDoBanco(internacao, dthrAltaMedicaBD, dtSaidaPacBD, tipoAltaMedicaBD);
		}
		if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosInformados()
				&& (DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTA.equals(exCode)
						|| DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_SAIDA.equals(exCode)
						|| DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTA_SAIDA.equals(exCode) || DarAltaPacienteCode.MENSAGEM_CONFIRMACAO_ALTERACAO_ALTA
						.equals(exCode))) {
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformados(false);
			flagsValidacaoDadosAltaPacienteVO.setMensagem(exCode.name());
			exCode = null;			
			return false;
		} else if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosFaturamento()
				&& DarAltaPacienteCode.MENSAGEM_LCTOS_FAT.equals(exCode)) {
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosFaturamento(false);
			flagsValidacaoDadosAltaPacienteVO.setMensagem(exCode.name());
			//exCode = null;
			throw new ApplicationBusinessException(DarAltaPacienteExceptionCode.MENSAGEM_LCTOS_FAT, exCode);
			//return false;
		} else if (flagsValidacaoDadosAltaPacienteVO.isValidaDadosDaAltaPaciente()
				&& DarAltaPacienteCode.PEDE_CONFIRMACAO_TIPO_ALTA.equals(exCode)) {
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosDaAltaPaciente(false);
			flagsValidacaoDadosAltaPacienteVO.setMensagem(exCode.name());
			exCode = null;
			return false;
		} else if (flagsValidacaoDadosAltaPacienteVO.isValidaPermissoesDarAltaPaciente()
				&& DarAltaPacienteCode.MENSAGEM_CONFIRMADA_PERMISSAO.equals(exCode)) {
			flagsValidacaoDadosAltaPacienteVO.setValidaPermissoesDarAltaPaciente(false);
			flagsValidacaoDadosAltaPacienteVO.setMensagem(exCode.name());
			exCode = null;
			return false;
		} else {
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformadosEstorno(true);
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosInformados(true);
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosFaturamento(true);
			flagsValidacaoDadosAltaPacienteVO.setValidaDadosDaAltaPaciente(true);
			flagsValidacaoDadosAltaPacienteVO.setValidaPermissoesDarAltaPaciente(true);
		}
		final AghParametros p = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_O);
		final String tamO = p.getVlrTexto();
		if (internacao.getTipoAltaMedica() != null && internacao.getTipoAltaMedica().getCodigo().equalsIgnoreCase(tamO)) {
			final String codigoTipoAltaMedica = this.alterarTipoAltaObito(internacao.getPaciente().getCodigo(), internacao.getSeq(),
					internacao.getDtSaidaPaciente(), internacao.getDthrAltaMedica());
			if (codigoTipoAltaMedica != null) {
				internacao.setTipoAltaMedica(getCadastrosBasicosInternacaoFacade().pesquisarTipoAltaMedicaPorCodigo(
						codigoTipoAltaMedica, null));
			}
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		internacao.setServidorDigita(servidorLogado);

		this.atualizarInternacao(internacao, nomeMicrocomputador);			

		return true;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}