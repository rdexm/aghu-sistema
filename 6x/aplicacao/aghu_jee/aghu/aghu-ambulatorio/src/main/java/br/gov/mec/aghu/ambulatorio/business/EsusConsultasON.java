package br.gov.mec.aghu.ambulatorio.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import utils.ThriftSerializer;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.ArquivosEsusVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.saude.esus.cds.transport.generated.thrift.atividadeindividual.FichaAtendimentoIndividualChildThrift;
import br.gov.saude.esus.cds.transport.generated.thrift.atividadeindividual.FichaAtendimentoIndividualMasterThrift;
import br.gov.saude.esus.cds.transport.generated.thrift.atividadeindividual.ProblemaCondicaoAvaliacaoAIThrift;
import br.gov.saude.esus.cds.transport.generated.thrift.common.UnicaLotacaoHeaderThrift;
import br.gov.saude.esus.cds.transport.generated.thrift.common.VariasLotacoesHeaderThrift;
import br.gov.saude.esus.cds.transport.generated.thrift.procedimento.FichaProcedimentoChildThrift;
import br.gov.saude.esus.cds.transport.generated.thrift.procedimento.FichaProcedimentoMasterThrift;
import br.gov.saude.esus.enums.AleitamentoMaterno;
import br.gov.saude.esus.enums.CiapCondicaoAvaliada;
import br.gov.saude.esus.enums.CondutaEncaminhamento;
import br.gov.saude.esus.enums.LocalDeAtendimento;
import br.gov.saude.esus.enums.ProcedimentoFicha;
import br.gov.saude.esus.enums.Sexo;
import br.gov.saude.esus.enums.TipoDadoSerializado;
import br.gov.saude.esus.enums.TipoDeAtendimento;
import br.gov.saude.esus.enums.Turno;
import br.gov.saude.esus.transport.common.api.configuracaodestino.VersaoThrift;
import br.gov.saude.esus.transport.common.generated.thrift.DadoInstalacaoThrift;
import br.gov.saude.esus.transport.common.generated.thrift.DadoTransporteThrift;

@Stateless
public class EsusConsultasON extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 8965264594576662465L;

	private static final Log LOG = LogFactory.getLog(EsusConsultasON.class);
	
	private static final String referenciaLog = "****";
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	
	public enum EsusConsultasONExceptionCode implements
			BusinessExceptionCode {

		PAC_UBS_ESUS_CBO_PROFISSIONAL, PAC_UBS_ESUS_CNS_PROFISSIONAL, PAC_UBS_ESUS_CNS_PACIENTE,PAC_UBS_ESUS_PRONTUARIO_PACIENTE,
		INFORMAR_AGENDA_ESUS_UBS,GERACAO_ESUS_RETORNO_DIFERENTE_10;
	}
	
	public ArquivosEsusVO gerarArquivoEsus(List<AacConsultas> consultas, Date dtInicio,
			Date dtFim, AghEspecialidades especialidade){
		List<DadoTransporteThrift> listaTransporte = new ArrayList<DadoTransporteThrift>();
		ArquivosEsusVO vo = new ArquivosEsusVO();
		List<String> listaErros = new ArrayList<String>();
		for (AacConsultas consulta : consultas) {
			try{
				AipPacientes paciente = this.getPacienteFacade().obterPacientePelaConsulta(consulta.getNumero());
				List<AacConsultaProcedHospitalar> procedimentos = this.getAmbulatorioFacade().buscarConsultaProcedHospPorNumeroConsulta(consulta.getNumero());
				boolean fichaAtendimentoIndividual = verificarNecessidadeFichaAtedimentoIndividual(especialidade,procedimentos);
				boolean fichaProcedimento = verificarNecessidadeFichaProcedimento(especialidade,procedimentos);			
				if(fichaAtendimentoIndividual){
					DadoTransporteThrift transporteAtendimentoIndividual = montarFichaAtendimentoIndividual(consulta,paciente,especialidade,procedimentos);
					listaTransporte.add(transporteAtendimentoIndividual);
				}
				if(fichaProcedimento){
					DadoTransporteThrift transporteFichaProcedimento = montarFichaProcedimento(consulta,paciente,procedimentos);
					listaTransporte.add(transporteFichaProcedimento);
				}
				
			}catch(BaseException e){
				listaErros.add(consulta.getNumero() + ";" + e.getMessage() + ";\n");
                
			}
		}		
		
		try {
			vo.setArquivoInconsistenciasEsus(montarInconsistencia(listaErros));
			vo.setArquivoEsus(generateZip(especialidade,listaTransporte));
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
		}
		return vo; 
		
	}


	private File montarInconsistencia(List<String> erros){
		File arquivo = new File("inconsistencias.csv");
		Writer writer = null;
		String cabecalho = "consulta;erro;\n";

		try {
		    writer = new FileWriter(arquivo);
		    writer.write(cabecalho);
		    for (String erro : erros) {
		    	writer.write(erro);
			}
		    
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
		} finally {
		   try {writer.close();} 
		   catch (Exception ex) {
			   LOG.error(ex.getMessage(),ex);	
		   }
		}
		
		return arquivo;
	}

	private DadoTransporteThrift montarFichaAtendimentoIndividual(AacConsultas consulta, AipPacientes paciente,AghEspecialidades especialidade ,List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException {
		UnicaLotacaoHeaderThrift unicaLotacao = new UnicaLotacaoHeaderThrift();
		this.montarCabecalho(unicaLotacao, consulta);
		FichaAtendimentoIndividualMasterThrift fichaAtendimentoMaster = new FichaAtendimentoIndividualMasterThrift();
		fichaAtendimentoMaster.setUuidFicha(this.getUuid(consulta));
		fichaAtendimentoMaster.setHeaderTransport(new VariasLotacoesHeaderThrift(unicaLotacao));		
		FichaAtendimentoIndividualChildThrift fichaAtendimento = new FichaAtendimentoIndividualChildThrift();
		this.montarPacienteFichaIndividual(fichaAtendimento,paciente);
		
		boolean contemProcedUrgencia = verificarProcedUrgencia(procedimentos);
		this.montarConsultaFichaIndividual(fichaAtendimento,consulta,especialidade,contemProcedUrgencia);		
		this.montarProcedimentosFichaIndividual(fichaAtendimento,consulta,procedimentos);
		fichaAtendimentoMaster.addToAtendimentosIndividuais(fichaAtendimento);
		byte[] dadoSerializado = ThriftSerializer.serialize(fichaAtendimentoMaster);
		DadoTransporteThrift dadoTransporteThrift = new DadoTransporteThrift();
		this.montarDadosTransporte(consulta,dadoTransporteThrift,dadoSerializado, TipoDadoSerializado.FICHA_ATENDIMENTO_INDIVIDUAL.getValor());
		return dadoTransporteThrift;
	}

	

	private boolean verificarProcedUrgencia(List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException {
		Integer procedAtendUrg = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PROCED_ATEND_URG);
		for (AacConsultaProcedHospitalar procedimento : procedimentos) {
			Integer phiSeq = procedimento.getProcedHospInterno().getSeq();
			if(phiSeq.equals(procedAtendUrg)){
				return true;
			}
		}
		return false;
	}


	private DadoTransporteThrift montarFichaProcedimento(AacConsultas consulta,AipPacientes paciente, List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException {
		UnicaLotacaoHeaderThrift unicaLotacao = new UnicaLotacaoHeaderThrift();
		this.montarCabecalho(unicaLotacao, consulta);
		FichaProcedimentoMasterThrift fichaProcedimentoMaster = new FichaProcedimentoMasterThrift();
		fichaProcedimentoMaster.setUuidFicha(this.getUuid(consulta));
		fichaProcedimentoMaster.setHeaderTransport(unicaLotacao);
		FichaProcedimentoChildThrift fichaProcedimento = new FichaProcedimentoChildThrift();
		this.montarPacienteFichaProcedimento(fichaProcedimento,paciente);
		this.montarConsultaFichaProcedimento(fichaProcedimento,consulta);		
		this.montarProcedimentosFichaProcedimento(fichaProcedimento,consulta,procedimentos);		
		fichaProcedimentoMaster.addToAtendProcedimentos(fichaProcedimento);
		byte[] dadoSerializado = ThriftSerializer.serialize(fichaProcedimentoMaster);
		DadoTransporteThrift dadoTransporteThrift = new DadoTransporteThrift();
		this.montarDadosTransporte(consulta,dadoTransporteThrift,dadoSerializado, TipoDadoSerializado.FICHA_PROCEDIMENTO.getValor());
		return dadoTransporteThrift;
	}

	


	public File generateZip(AghEspecialidades esp,List<DadoTransporteThrift> dadoTransporteThrift) throws ApplicationBusinessException {
		File zipFile = new File(this.getNomeArquivoEsus(esp));
		ZipOutputStream outputStream = null;

		try {
			outputStream = new ZipOutputStream(new FileOutputStream(zipFile));
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(),e);
		}

		if (outputStream != null) {
			for (DadoTransporteThrift thrift : dadoTransporteThrift) {
				try {
					String entryName = resolveZipEntry(thrift);
					outputStream.putNextEntry(new ZipEntry(entryName));
					byte[] data = ThriftSerializer.serialize(thrift);
					outputStream.write(data);
				} catch (IOException e) {
					LOG.error(e.getMessage(),e);
				}
				
			}
		}
		try {
			outputStream.closeEntry();
			outputStream.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
		}
		return zipFile;
	}

	private String resolveZipEntry(DadoTransporteThrift thrift) throws ApplicationBusinessException {
		return thrift.getUuidDadoSerializado() + this.getExtensaoExportEsus();
	}
	
	public String getCnesUBS() throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_CNES);
	}
	public String getRazaoSocialUBS()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_RAZAO_SOCIAL);
	}
	public String getFoneUBS()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_FONE);
	}
	public String getEmailResponsavelUBS()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_EMAIL_RESPONSAVEL);
	}
	public String getCnpjUBS()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_CNPJ);
	}
	public String getContraChaveUBS()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_CONTRA_CHAVE);
	}
	public String getUuidInstalacaoUBS()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_UUID_INSTALACAO);
	}
	public String getRazaoSocialRemetente()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_RAZAO_SOCIAL_REMETENTE);
	}
	public String getFoneRemetente()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_FONE_REMETENTE);
	}
	public String getEmailResponsavelRemetente()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_EMAIL_RESPONSAVEL_REMETENTE);
	}
	public String getCnpjRemetente()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_CNPJ_CPF_REMETENTE);
	}
	public String getContraChaveRemetente()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_CONTRA_CHAVE_REMETENTE);
	}
	public String getUuidInstalacaoRemetente()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_UUID_INSTALACAO_REMETENTE);
	}
	public String getExtensaoExportEsus()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_EXTENSAO_ARQUIVO_ESUS);
	}
	public String getNomeArquivoEsus(AghEspecialidades esp)throws ApplicationBusinessException{
		return "agenda-" + esp.getSigla()+ "-" +getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_NOME_ARQUIVO_ESUS) ;
	}
	public VersaoThrift getVersao()throws ApplicationBusinessException{
		int major = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_UBS_ESUS_VERSAO_THRIFT_MAJOR);
		int minor = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_UBS_ESUS_VERSAO_THRIFT_MINOR);
		int revision = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_UBS_ESUS_VERSAO_THRIFT_REVISION);
		return new VersaoThrift(major, minor, revision);
	}
	
	
	public DadoInstalacaoThrift getOriginadora() throws ApplicationBusinessException{
		DadoInstalacaoThrift originadora = new DadoInstalacaoThrift();
		originadora.setContraChave(getContraChaveUBS());
		originadora.setCpfOuCnpj(getCnpjUBS());
		originadora.setEmail(getEmailResponsavelUBS());
		originadora.setFone(getFoneUBS());
		originadora.setNomeOuRazaoSocial(getRazaoSocialUBS());
		originadora.setUuidInstalacao(getUuidInstalacaoUBS());
		return originadora;
	}
	public DadoInstalacaoThrift getRemetente() throws ApplicationBusinessException{
		DadoInstalacaoThrift remetente = new DadoInstalacaoThrift();
		remetente.setContraChave(this.getContraChaveRemetente());
		remetente.setCpfOuCnpj(this.getCnpjRemetente());
		remetente.setEmail(this.getEmailResponsavelRemetente());
		remetente.setFone(this.getFoneRemetente());
		remetente.setNomeOuRazaoSocial(this.getRazaoSocialRemetente());
		remetente.setUuidInstalacao(this.getUuidInstalacaoRemetente());
		return remetente;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected IRegistroColaboradorFacade getColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IPacienteFacade getPacienteFacade(){
		return this.pacienteFacade;
	}

	public String getIneUBS() throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_INE);
	}
	public String getCodIbgeMunicipioUBS() throws ApplicationBusinessException{
		return getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_UBS_ESUS_COD_IBGE);
	}
	public Integer getVacemdia()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_VAC_EM_DIA);
	}
	public Integer getCriAleMat()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_CRI_ALE_MAT);
	}
	public Integer getProConAvaAsm()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_ASM);
	}
	public Integer getProConAvaDes()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DES);
	}
	public Integer getProConAvaDia1()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DIA_1);
	}
	public Integer getProConAvaDia2()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DIA_2);
	}
	public Integer getProConAvaDPO()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DPO);
	}
	public Integer getProConAvaHipArt1()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_HIP_ART_1);
	}
	public Integer getProConAvaHipArt2()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_HIP_ART_2);
	}
	public Integer getProConAvaObe()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_OBE);
	}
	public Integer getProConAvaPre()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_PRE);
	}
	public Integer getProConAvaPuePueat42dia()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_PUE_PUE_AT_42_DIA);
	}
	public Integer getProConAvaSauSexeRep()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_SAU_SEX_E_REP);
	}
	public Integer getProConAvaTab()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_TAB);
	}
	public Integer getProConAvaUsudealc()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_USU_DE_ALC);
	}
	public Integer getProConAvaUsudeoutdro()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_USU_DE_OUT_DRO);
	}
	public Integer getProConAvaSauMen()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_SAU_MEN);
	}
	public Integer getProConAvaRea()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_REA);
	}
	public Integer getProConAvaDoeTraTub()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DOE_TRA_TUB);
	}
	public Integer getProConAvaDoeTraHan()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DOE_TRA_HAN);
	}
	public Integer getProConAvaDoeTraDen()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DOE_TRA_DEN);
	}
	public Integer getProConAvaDoeTraDST()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_DOE_TRA_DST);
	}
	public Integer getProConAvaRasCandoColdoUte()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_RAS_CAN_DO_COL_DO_UTE);
	}
	public Integer getProConAvaRasRiscar()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_PRO_CON_AVA_RAS_RIS_CAR);
	}
	public Integer getExaSoleAvaAEle()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_ELE);
	}
	public Integer getExaSoleAvaAExadeesc()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_EXA_DE_ESC);
	}
	public Integer getExaSoleAvaAGli()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_GLI);
	}
	public Integer getExaSoleAvaASordeSifVD()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_SOR_DE_SIF_VD);
	}
	public Integer getExaSoleAvaASorparHIV1()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_SOR_PAR_HIV_1);
	}
	public Integer getExaSoleAvaASorparHIV2()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_SOR_PAR_HIV_2);
	}
	public Integer getExaSoleAvaATesdeGra()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_TES_DE_GRA);
	}
	public Integer getExaSoleAvaATesdopez()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_EXA_SOL_E_AVA_A_TES_DO_PEZ);
	}
	public Integer getFicemObs()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_FIC_EM_OBS);
	}
	public Integer getConEncEncpSerEsp()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_CON_ENC_ENC_P_SER_ESP);
	}
	public Integer getConEncEncpUrg()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FAI_CON_ENC_ENC_P_URG);
	}
	public Integer getProCirAcucomInsdeAgu()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_ACU_COM_INS_DE_AGU);
	}
	public Integer getProCirCatVesdeAli()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_CAT_VES_DE_ALI);
	}
	public Integer getProCirCauQuidePeqLes()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_CAU_QUI_DE_PEQ_LES);
	}
	public Integer getProCirCirdeUnhCa()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_CIR_DE_UNH_CA);
	}
	public Integer getProCirCuideEst()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_CUI_DE_EST);
	}
	public Integer getProCirCurEsp()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_CUR_ESP);
	}
	public Integer getProCirDredeAbs()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_DRE_DE_ABS);
	}
	public Integer getProCirEle()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_ELE);
	}
	public Integer getProCirColdeCitdeColUte()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_COL_DE_CIT_DE_COL_UTE);
	}
	public Integer getProCirExadoPeDia()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_EXA_DO_PE_DIA);
	}
	public Integer getProCirExedeTumSupdePel()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_EXE_DE_TUM_SUP_DE_PEL);
	}
	public Integer getProCirFunExdeFundeOlh()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_FUN_EX_DE_FUN_DE_OLH);
	}
	public Integer getProCirInfemCavSin()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_INF_EM_CAV_SIN);
	}
	public Integer getProCirRemdeCorEstdaCavAudeNas()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_REM_DE_COR_EST_DA_CAV_AUD_E_NAS);
	}
	public Integer getProCirRemdeCorEstSub()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_REM_DE_COR_EST_SUB);
	}
	public Integer getProCirRetdeCer()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_RET_DE_CER);
	}
	public Integer getProCirRetdePondeCir()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_RET_DE_PON_DE_CIR);
	}
	public Integer getProCirSutSim()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_SUT_SIM);
	}
	public Integer getProCirTriOft()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_TRI_OFT);
	}
	public Integer getProCirTamdeEpi()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_TAM_DE_EPI);
	}
	public Integer getProCirTesRapDeGra()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_TES_RAP_DE_GRA);
	}
	public Integer getProCirTesRapParHIV()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_TES_RAP_PAR_HIV);
	}
	public Integer getProCirTesRapParHepC()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_TES_RAP_PAR_HEP_C);
	}
	public Integer getProCirTesRapParSif()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_CIR_TES_RAP_PAR_SIF);
	}
	public Integer getProAdmdeMedOra()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_ADM_DE_MED_ORA);
	}
	public Integer getProAdmdeMedInt()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_ADM_DE_MED_INT);
	}
	public Integer getProAdmdeMedEndIna1()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_ADM_DE_MED_END_INA_1);
	}
	public Integer getProAdmdeMedEndIna2()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_ADM_DE_MED_END_INA_2);
	}
	public Integer getProAdmdeMedTop()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_ADM_DE_MED_TOP);
	}
	public Integer getProAdmdeMedPenparTradeSif()throws ApplicationBusinessException{
		return getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_ESUS_FP_PRO_ADM_DE_MED_PEN_PAR_TRA_DE_SIF);
	}
	public String getUuid(AacConsultas consulta){
		return consulta.getNumero() + "-" +UUID.randomUUID();
	}
	
	private void montarCabecalho(UnicaLotacaoHeaderThrift lotacao, AacConsultas consulta) throws ApplicationBusinessException{
		Short infoTipoCBO = this.getParametroFacade().buscarValorShort(AghuParametrosEnum.P_TIPO_INF_CBO);
		Short infoTipoCNS = this.getParametroFacade().buscarValorShort(AghuParametrosEnum.P_TIPO_INF_CNS);
		RapServidores profissionalResponsavel = consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel();
		if(profissionalResponsavel == null){
			profissionalResponsavel = consulta.getGradeAgendamenConsulta().getProfServidor();
		}
		RapPessoasFisicas pessoaFisica = this.getColaboradorFacade().obterRapPessoasFisicasPorServidor(profissionalResponsavel.getId());
		
		RapPessoaTipoInformacoes infoCBOProfissional = this.getColaboradorFacade().obterTipoInformacao(pessoaFisica.getCodigo(),infoTipoCBO);
		if(infoCBOProfissional == null || infoCBOProfissional.getValor() == null){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.PAC_UBS_ESUS_CBO_PROFISSIONAL,profissionalResponsavel.getId().getMatricula(),profissionalResponsavel.getId().getVinCodigo());
		}
		RapPessoaTipoInformacoes infoCNSProfissional = this.getColaboradorFacade().obterTipoInformacao(pessoaFisica.getCodigo(),infoTipoCNS);
		if(infoCNSProfissional == null || infoCNSProfissional.getValor() == null){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.PAC_UBS_ESUS_CNS_PROFISSIONAL,profissionalResponsavel.getId().getMatricula(),profissionalResponsavel.getId().getVinCodigo());
		}
		LOG.warn(referenciaLog + " profissional esus consulta " + consulta.getNumero() + referenciaLog);
		LOG.warn(referenciaLog + " CBO " + infoCBOProfissional.getValor() + referenciaLog);
		LOG.warn(referenciaLog + " CNS " + infoCNSProfissional.getValor() + referenciaLog);
		LOG.warn(referenciaLog + " INE " + this.getIneUBS() + referenciaLog);
		LOG.warn(referenciaLog + " MATRICULA " + profissionalResponsavel.getId().getMatricula() + referenciaLog);
		LOG.warn(referenciaLog + " VINCULO " + profissionalResponsavel.getId().getVinCodigo() + referenciaLog);
		lotacao.setCboCodigo_2002(infoCBOProfissional.getValor());
		lotacao.setProfissionalCNS(infoCNSProfissional.getValor());
		lotacao.setCnes(this.getCnesUBS());
		lotacao.setIne(this.getIneUBS());
		lotacao.setIneIsSet(true);
		lotacao.setCodigoIbgeMunicipio(this.getCodIbgeMunicipioUBS());
	}
	
	private void montarPacienteFichaIndividual(FichaAtendimentoIndividualChildThrift fichaAtendimento, AipPacientes paciente) throws ApplicationBusinessException{
		if(paciente.getNroCartaoSaude() == null){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.PAC_UBS_ESUS_CNS_PACIENTE,paciente.getProntuario());
		}
		fichaAtendimento.setCns(paciente.getNroCartaoSaude().toString());
		fichaAtendimento.setDataNascimento(paciente.getDtNascimento().getTime());
		if(paciente.getProntuario() == null){
			
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.PAC_UBS_ESUS_PRONTUARIO_PACIENTE,paciente.getProntuario());
		}
		fichaAtendimento.setNumeroProntuario(paciente.getProntuario().toString());
		
		fichaAtendimento.setSexo(paciente.getSexoBiologico() == DominioSexo.M ? Sexo.FEMININO.getValor() : Sexo.MASCULINO.getValor());
	}
	
	private void montarConsultaFichaIndividual(FichaAtendimentoIndividualChildThrift fichaAtendimento, AacConsultas consulta,AghEspecialidades especialidade,boolean atendimentoUrgencia) throws ApplicationBusinessException{
		fichaAtendimento.setLocalDeAtendimento(LocalDeAtendimento.UBS.getValor());
		DominioTurno turno = this.getAmbulatorioFacade().defineTurno(consulta.getDtConsulta());
		fichaAtendimento.setTurno(turno == DominioTurno.M ? Turno.MATUTINO.getValor() : (turno == DominioTurno.T ? Turno.VESPERTINO.getValor() : Turno.NOTURNO.getValor()));
		String agendaACT = this.getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_ESUS_FAI_AGENDA_ACT);
		if(especialidade.getSigla().equals(agendaACT)){
			fichaAtendimento.setTipoAtendimento(TipoDeAtendimento.ESCUTA_INICIAL_OU_ORIENTACAO.getValor());
		}else{
			if(atendimentoUrgencia){
				fichaAtendimento.setTipoAtendimento(TipoDeAtendimento.ATENDIMENTO_DE_URGENCIA.getValor());
			}else{
				fichaAtendimento.setTipoAtendimento(TipoDeAtendimento.CONSULTA_NO_DIA.getValor());
			}
		}
			
		
	}
	
	private void montarProcedimentosFichaIndividual(FichaAtendimentoIndividualChildThrift fichaAtendimento,AacConsultas consulta, List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException{
		
		List<String> ciaps = new ArrayList<String>();
		List<String> exames = new ArrayList<String>();
		List<Long> condutas = new ArrayList<Long>();
		for (AacConsultaProcedHospitalar procedimento : procedimentos) {
			Integer phiSeq = procedimento.getProcedHospInterno().getSeq();
			montarProcedimentosFichaIndividualSubParte1(phiSeq,fichaAtendimento,ciaps);
			montarProcedimentosFichaIndividualSubParte2(phiSeq,fichaAtendimento,ciaps);
			montarProcedimentosFichaIndividualSubParte3(phiSeq,fichaAtendimento,ciaps,condutas);
		}
		if(!ciaps.isEmpty()){
			ProblemaCondicaoAvaliacaoAIThrift problema =  new ProblemaCondicaoAvaliacaoAIThrift();
			problema.setCiaps(ciaps);
			fichaAtendimento.setProblemaCondicaoAvaliada(problema);
		}
		if(!exames.isEmpty()){
			fichaAtendimento.setExamesAvaliados(exames);
			fichaAtendimento.setExamesSolicitados(exames);
		}
		if(condutas.isEmpty()){
			condutas.add(CondutaEncaminhamento.RETORNO_PARA_CONSULTA_AGENDADA.getValor());
		}
		fichaAtendimento.setCondutas(condutas);
	}
	
	private void montarProcedimentosFichaIndividualSubParte3(Integer phiSeq,
			FichaAtendimentoIndividualChildThrift fichaAtendimento,
			List<String> ciaps, List<Long> condutas) throws ApplicationBusinessException {
		if(phiSeq.equals(this.getProConAvaRasCandoColdoUte())){
			ciaps.add(CiapCondicaoAvaliada.ABP022.getValor());
		}else if(phiSeq.equals(this.getProConAvaRasRiscar())){
			ciaps.add(CiapCondicaoAvaliada.ABP024.getValor());
		}else if(phiSeq.equals(this.getFicemObs())){
			fichaAtendimento.setFicouEmObservacao(true);
		}else if(phiSeq.equals(this.getConEncEncpSerEsp())){
			condutas.add(CondutaEncaminhamento.ENCAMINHAMENTO_PARA_SERVICO_ESPECIALIZADO.getValor());
		}else if(phiSeq.equals(this.getConEncEncpUrg())){
			condutas.add(CondutaEncaminhamento.ENCAMINHAMENTO_PARA_URGENCIA.getValor());
		}    
		
	}


	private void montarProcedimentosFichaIndividualSubParte2(Integer phiSeq,
			FichaAtendimentoIndividualChildThrift fichaAtendimento,
			List<String> ciaps) throws ApplicationBusinessException {
		if(phiSeq.equals(this.getProConAvaPuePueat42dia())){
			ciaps.add(CiapCondicaoAvaliada.ABP002.getValor());
		}else if(phiSeq.equals(this.getProConAvaSauSexeRep())){
			ciaps.add(CiapCondicaoAvaliada.ABP003.getValor());
		}else if(phiSeq.equals(this.getProConAvaTab())){
			ciaps.add(CiapCondicaoAvaliada.ABP011.getValor());
		}else if(phiSeq.equals(this.getProConAvaUsudealc())){
			ciaps.add(CiapCondicaoAvaliada.ABP012.getValor());
		}else if(phiSeq.equals(this.getProConAvaUsudeoutdro())){
			ciaps.add(CiapCondicaoAvaliada.ABP013.getValor());
		}else if(phiSeq.equals(this.getProConAvaSauMen())){
			ciaps.add(CiapCondicaoAvaliada.ABP014.getValor());
		}else if(phiSeq.equals(this.getProConAvaRea())){
			ciaps.add(CiapCondicaoAvaliada.ABP015.getValor());
		}else if(phiSeq.equals(this.getProConAvaDoeTraTub())){
			ciaps.add(CiapCondicaoAvaliada.ABP017.getValor());
		}else if(phiSeq.equals(this.getProConAvaDoeTraHan())){
			ciaps.add(CiapCondicaoAvaliada.ABP018.getValor());
		}else if(phiSeq.equals(this.getProConAvaDoeTraDen())){
			ciaps.add(CiapCondicaoAvaliada.ABP019.getValor());
		}else if(phiSeq.equals(this.getProConAvaDoeTraDST())){
			ciaps.add(CiapCondicaoAvaliada.ABP020.getValor());
		}
	}


	private void montarProcedimentosFichaIndividualSubParte1(Integer phiSeq,
			FichaAtendimentoIndividualChildThrift fichaAtendimento,
			List<String> ciaps) throws ApplicationBusinessException {
		if(phiSeq.equals(this.getVacemdia())){
			fichaAtendimento.setVacinaEmDia(true);
		}else if(phiSeq.equals(this.getCriAleMat())){
			fichaAtendimento.setAleitamentoMaterno(AleitamentoMaterno.COMPLEMENTADO.getValor());
		}else if(phiSeq.equals(this.getProConAvaAsm())){
			ciaps.add(CiapCondicaoAvaliada.ABP009.getValor());
		}else if(phiSeq.equals(this.getProConAvaDes())){
			ciaps.add(CiapCondicaoAvaliada.ABP008.getValor());
		}else if(phiSeq.equals(this.getProConAvaDia1()) || phiSeq.equals(this.getProConAvaDia2())){
			ciaps.add(CiapCondicaoAvaliada.ABP006.getValor());
		}else if(phiSeq.equals(this.getProConAvaDPO())){
			ciaps.add(CiapCondicaoAvaliada.ABP010.getValor());
		}else if(phiSeq.equals(this.getProConAvaHipArt1()) || phiSeq.equals(this.getProConAvaHipArt2())){
			ciaps.add(CiapCondicaoAvaliada.ABP005.getValor());
		}else if(phiSeq.equals(this.getProConAvaObe())){
			ciaps.add(CiapCondicaoAvaliada.ABP007.getValor());
		}else if(phiSeq.equals(this.getProConAvaPre())){
			ciaps.add(CiapCondicaoAvaliada.ABP001.getValor());
		}		
	}


	private void montarDadosTransporte(AacConsultas consulta,DadoTransporteThrift dadoTransporteThrift, byte[] dadoSerializado,Long valor) throws ApplicationBusinessException {
		dadoTransporteThrift.setUuidDadoSerializado(this.getUuid(consulta));
		dadoTransporteThrift.setTipoDadoSerializado(valor);
		dadoTransporteThrift.setVersao(this.getVersao());
		dadoTransporteThrift.setOriginadora(this.getOriginadora());
		dadoTransporteThrift.setRemetente(this.getRemetente());
		dadoTransporteThrift.setCnesDadoSerializado(this.getCnesUBS());
        dadoTransporteThrift.setDadoSerializado(dadoSerializado);
	}
	
	private void montarPacienteFichaProcedimento(FichaProcedimentoChildThrift fichaProcedimento, AipPacientes paciente) throws ApplicationBusinessException{
		if(paciente.getNroCartaoSaude() == null){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.PAC_UBS_ESUS_CNS_PACIENTE,paciente.getProntuario() == null ? paciente.getNome() : paciente.getProntuario());
		}
		fichaProcedimento.setNumCartaoSus(paciente.getNroCartaoSaude().toString());
		fichaProcedimento.setDtNascimento(paciente.getDtNascimento().getTime());
		if(paciente.getProntuario() == null){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.PAC_UBS_ESUS_PRONTUARIO_PACIENTE);
		}
		fichaProcedimento.setNumProntuario(paciente.getProntuario().toString());
		fichaProcedimento.setSexo(paciente.getSexoBiologico() == DominioSexo.M ? Sexo.FEMININO.getValor() : Sexo.MASCULINO.getValor());
	}
	
	private void montarConsultaFichaProcedimento(FichaProcedimentoChildThrift fichaProcedimento, AacConsultas consulta) throws ApplicationBusinessException{
		fichaProcedimento.setLocalAtendimento(LocalDeAtendimento.UBS.getValor());
		DominioTurno turno = this.getAmbulatorioFacade().defineTurno(consulta.getDtConsulta());
		fichaProcedimento.setTurno(turno == DominioTurno.M ? Turno.MATUTINO.getValor() : (turno == DominioTurno.T ? Turno.VESPERTINO.getValor() : Turno.NOTURNO.getValor()));
	}
	
	
	private void montarProcedimentosFichaProcedimento(FichaProcedimentoChildThrift fichaProcedimento,AacConsultas consulta, List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException{
		List<String> procedimentosPequenasCirurgias = new ArrayList<String>();
		for (AacConsultaProcedHospitalar procedimento : procedimentos) {
			Integer phiSeq = procedimento.getProcedHospInterno().getSeq();
			montarProcedimentosFichaProcedimentoSubParte1(phiSeq,procedimentosPequenasCirurgias);
			montarProcedimentosFichaProcedimentoSubParte2(phiSeq,procedimentosPequenasCirurgias);
			montarProcedimentosFichaProcedimentoSubParte3(phiSeq,procedimentosPequenasCirurgias);
			
			if(!procedimentosPequenasCirurgias.isEmpty()){
				fichaProcedimento.setProcedimentos(procedimentosPequenasCirurgias);
			}
		}
	}
	

	
	
	
	
	private void montarProcedimentosFichaProcedimentoSubParte3(Integer phiSeq,
			List<String> procedimentosPequenasCirurgias) throws ApplicationBusinessException {
		if(phiSeq.equals(this.getProCirTamdeEpi())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG021.getValor());
		}else if(phiSeq.equals(this.getProCirTesRapDeGra())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG022.getValor());
		}else if(phiSeq.equals(this.getProCirTesRapParHIV())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG024.getValor());
		}else if(phiSeq.equals(this.getProCirTesRapParHepC())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG025.getValor());
		}else if(phiSeq.equals(this.getProCirTesRapParSif())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG026.getValor());
		}else if(phiSeq.equals(this.getProAdmdeMedOra())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG027.getValor());
		}else if(phiSeq.equals(this.getProAdmdeMedInt())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG028.getValor());
		}else if(phiSeq.equals(this.getProAdmdeMedEndIna1()) || phiSeq.equals(this.getProAdmdeMedEndIna2())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG030.getValor());
		}else if(phiSeq.equals(this.getProAdmdeMedTop())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG031.getValor());
		}else if(phiSeq.equals(this.getProAdmdeMedPenparTradeSif())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG032.getValor());
		}
		
	}


	private void montarProcedimentosFichaProcedimentoSubParte2(Integer phiSeq,
			List<String> procedimentosPequenasCirurgias) throws ApplicationBusinessException {
		if(phiSeq.equals(this.getProCirExadoPeDia())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG011.getValor());
		}else if(phiSeq.equals(this.getProCirExedeTumSupdePel())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG012.getValor());
		}else if(phiSeq.equals(this.getProCirFunExdeFundeOlh())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG013.getValor());
		}else if(phiSeq.equals(this.getProCirInfemCavSin())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG014.getValor());
		}else if(phiSeq.equals(this.getProCirRemdeCorEstdaCavAudeNas())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG015.getValor());
		}else if(phiSeq.equals(this.getProCirRemdeCorEstSub())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG016.getValor());
		}else if(phiSeq.equals(this.getProCirRetdeCer())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG017.getValor());
		}else if(phiSeq.equals(this.getProCirRetdePondeCir())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG018.getValor());
		}else if(phiSeq.equals(this.getProCirSutSim())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG019.getValor());
		}else if(phiSeq.equals(this.getProCirTriOft())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG020.getValor());
		}
		
	}


	private void montarProcedimentosFichaProcedimentoSubParte1(Integer phiSeq,
			List<String> procedimentosPequenasCirurgias) throws ApplicationBusinessException {
		if(phiSeq.equals(this.getProCirAcucomInsdeAgu())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG001.getValor());
		}else if(phiSeq.equals(this.getProCirCatVesdeAli())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG003.getValor());
		}else if(phiSeq.equals(this.getProCirCauQuidePeqLes())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG004.getValor());
		}else if(phiSeq.equals(this.getProCirCirdeUnhCa())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG005.getValor());
		}else if(phiSeq.equals(this.getProCirCuideEst())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG006.getValor());
		}else if(phiSeq.equals(this.getProCirDredeAbs())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG008.getValor());
		}else if(phiSeq.equals(this.getProCirEle())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABEX004.getValor());
		}else if(phiSeq.equals(this.getProCirColdeCitdeColUte())){
			procedimentosPequenasCirurgias.add(ProcedimentoFicha.ABPG010.getValor());
		}
		
	}


	public void verificarAgendasEsus(AghEspecialidades especialidade, AacRetornos retorno) throws ApplicationBusinessException{
		if(especialidade == null){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.INFORMAR_AGENDA_ESUS_UBS);
		}
		if(retorno == null ){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.GERACAO_ESUS_RETORNO_DIFERENTE_10);
		}
		String[] siglasAgendasFP = this.getParametroFacade().buscarValorArray(AghuParametrosEnum.P_ESUS_FP_AGENDAS);
		String[] siglasAgendasFAI = this.getParametroFacade().buscarValorArray(AghuParametrosEnum.P_ESUS_FAI_AGENDAS);
		boolean contem = false;
		for (String agenda : siglasAgendasFP) {
			if(especialidade.getSigla().equals(agenda)){
				contem = true;
				break;
			}
		}
		if(contem){
			return;
		}
		for (String agenda : siglasAgendasFAI) {
			if(especialidade.getSigla().equals(agenda)){
				contem = true;
				break;
			}
		}
		if(!contem){
			throw new ApplicationBusinessException(EsusConsultasONExceptionCode.INFORMAR_AGENDA_ESUS_UBS);
		}
	}
	
	
	
	
	private boolean verificarNecessidadeFichaProcedimento(
			AghEspecialidades especialidade,
			List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException{ 
		String[] siglasAgendasFP = this.getParametroFacade().buscarValorArray(AghuParametrosEnum.P_ESUS_FP_AGENDAS);
		for (String agenda : siglasAgendasFP) {
			if(especialidade.getSigla().equals(agenda)){
				return true;
			}
		}
		for (AacConsultaProcedHospitalar procedimento : procedimentos) {
			Integer phiSeq = procedimento.getProcedHospInterno().getSeq();
			if(verificarNecessidadeFichaProcedimentoSubParte1(phiSeq) ||
					verificarNecessidadeFichaProcedimentoSubParte2(phiSeq) || verificarNecessidadeFichaProcedimentoSubParte3(phiSeq)){
				return true;
			}
		}
		return false;
	}

	private boolean verificarNecessidadeFichaProcedimentoSubParte2(
			Integer phiSeq) throws ApplicationBusinessException {
		return phiSeq.equals(this.getProCirInfemCavSin())  || 
				phiSeq.equals(this.getProCirRemdeCorEstdaCavAudeNas())  || phiSeq.equals(this.getProCirRemdeCorEstSub()) || phiSeq.equals(this.getProCirRetdeCer()) || 
				phiSeq.equals(this.getProCirRetdePondeCir()) || phiSeq.equals(this.getProCirSutSim()) || phiSeq.equals(this.getProCirTriOft()) || 
				phiSeq.equals(this.getProCirTamdeEpi()) || phiSeq.equals(this.getProCirTesRapDeGra()) || phiSeq.equals(this.getProCirTesRapParHIV()) || 
				phiSeq.equals(this.getProCirTesRapParHepC()) || phiSeq.equals(this.getProCirTesRapParSif());
	}


	private boolean verificarNecessidadeFichaProcedimentoSubParte3(
			Integer phiSeq) throws ApplicationBusinessException {
		return phiSeq.equals(this.getProAdmdeMedOra()) || 
				phiSeq.equals(this.getProAdmdeMedInt()) || phiSeq.equals(this.getProAdmdeMedEndIna1()) || phiSeq.equals(this.getProAdmdeMedEndIna2()) || 
				phiSeq.equals(this.getProAdmdeMedTop()) || phiSeq.equals(this.getProAdmdeMedPenparTradeSif());
	}


	private boolean verificarNecessidadeFichaProcedimentoSubParte1(
			Integer phiSeq) throws ApplicationBusinessException {
		return phiSeq.equals(this.getProCirAcucomInsdeAgu()) || phiSeq.equals(this.getProCirCatVesdeAli()) || phiSeq.equals(this.getProCirCauQuidePeqLes())  || 
				phiSeq.equals(this.getProCirCirdeUnhCa())  || phiSeq.equals(this.getProCirCuideEst())  ||  phiSeq.equals(this.getProCirDredeAbs()) || 
				phiSeq.equals(this.getProCirEle())  || phiSeq.equals(this.getProCirColdeCitdeColUte())  || phiSeq.equals(this.getProCirExadoPeDia())  || 
				phiSeq.equals(this.getProCirExedeTumSupdePel())  || phiSeq.equals(this.getProCirFunExdeFundeOlh()) ;
	}


	private boolean verificarNecessidadeFichaAtedimentoIndividual(
			AghEspecialidades especialidade,
			List<AacConsultaProcedHospitalar> procedimentos) throws ApplicationBusinessException{ 
		String[] siglasAgendasFAI = this.getParametroFacade().buscarValorArray(AghuParametrosEnum.P_ESUS_FAI_AGENDAS);
		for (String agenda : siglasAgendasFAI) {
			if(especialidade.getSigla().equals(agenda)){
				return true;
			}
		}
		for (AacConsultaProcedHospitalar procedimento : procedimentos) {
			Integer phiSeq = procedimento.getProcedHospInterno().getSeq();
			if(verificarNecessidadeFichaAtedimentoIndividualSubGrupo1(phiSeq) ||
					verificarNecessidadeFichaAtedimentoIndividualSubGrupo2(phiSeq)  || verificarNecessidadeFichaAtedimentoIndividualSubGrupo3(phiSeq) ){
				return true;
			}
				
		}
		return false;
	}


	private boolean verificarNecessidadeFichaAtedimentoIndividualSubGrupo2(
			Integer phiSeq) throws ApplicationBusinessException {
		return phiSeq.equals(this.getProConAvaUsudeoutdro()) || phiSeq.equals(this.getProConAvaSauMen()) || 
				phiSeq.equals(this.getProConAvaRea()) || phiSeq.equals(this.getProConAvaDoeTraTub()) || phiSeq.equals(this.getProConAvaDoeTraHan()) || phiSeq.equals(this.getProConAvaDoeTraDen())  || 
				phiSeq.equals(this.getProConAvaDoeTraDST()) || phiSeq.equals(this.getProConAvaRasCandoColdoUte()) || phiSeq.equals(this.getProConAvaRasRiscar()) || phiSeq.equals(this.getExaSoleAvaASordeSifVD());
	}


	private boolean verificarNecessidadeFichaAtedimentoIndividualSubGrupo3(
			Integer phiSeq) throws ApplicationBusinessException {
		return phiSeq.equals(this.getExaSoleAvaASorparHIV1()) || phiSeq.equals(this.getExaSoleAvaASorparHIV2())  || phiSeq.equals(this.getExaSoleAvaATesdeGra()) || phiSeq.equals(this.getExaSoleAvaATesdopez())  || 
				phiSeq.equals(this.getFicemObs()) || phiSeq.equals(this.getConEncEncpSerEsp()) || phiSeq.equals(this.getConEncEncpUrg());
	}


	private boolean verificarNecessidadeFichaAtedimentoIndividualSubGrupo1(
			Integer phiSeq) throws ApplicationBusinessException {
		return phiSeq.equals(this.getVacemdia()) || phiSeq.equals(this.getCriAleMat()) || phiSeq.equals(this.getProConAvaAsm()) || phiSeq.equals(this.getProConAvaDes())  || 
				phiSeq.equals(this.getProConAvaDia1()) || phiSeq.equals(this.getProConAvaDia2()) || phiSeq.equals(this.getProConAvaDPO()) || phiSeq.equals(this.getProConAvaHipArt1()) || phiSeq.equals(this.getProConAvaHipArt2())  || 
				phiSeq.equals(this.getProConAvaObe())  ||  phiSeq.equals(this.getProConAvaPre()) || phiSeq.equals(this.getProConAvaPuePueat42dia()) || phiSeq.equals(this.getProConAvaSauSexeRep())  || 
				phiSeq.equals(this.getProConAvaTab()) || phiSeq.equals(this.getProConAvaUsudealc());
	}
	
	
	
}