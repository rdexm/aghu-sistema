package br.gov.mec.aghu.faturamento.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoRegistroDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GerarArquivoSmsON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5600589918150758580L;

	private static final Log LOG = LogFactory.getLog(GerarArquivoSmsON.class);

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private FaturamentoRN faturamentoRN;

	@EJB
	private FatkCthnRN fatkCthnRN;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@Inject
	private FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO;
			
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;

	@Inject 
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;
	
	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;
	
	protected enum GerarArquivoSmsONExceptionCode implements BusinessExceptionCode {
		ERRO_GERAR_ARQ_SMS;
	}

	private static final String QUEBRA_LINHA = "line.separator";
	private static final String ANO_MES_DIA = "yyyyMMdd";
	
	public ByteArrayOutputStream gerarArquivoSms(Date data) throws ApplicationBusinessException {
		try {
			Short grupoOpm = parametroFacade.buscarValorShort(AghuParametrosEnum.P_GRUPO_OPM);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gerarArquivo(data, baos, grupoOpm);
			return baos;
		} catch (IOException e) {
			throw new ApplicationBusinessException(GerarArquivoSmsONExceptionCode.ERRO_GERAR_ARQ_SMS);
		}	
	}
	
	private void gerarArquivo(Date data, ByteArrayOutputStream writer, Short grupoOpm) throws ApplicationBusinessException, IOException {
		
		inserirLinhaArqCabecalho(data, grupoOpm, writer);
		
		List<FatContasHospitalares> contas = fatContasHospitalaresDAO.obterContasDataEnvioSms(data);
		if(contas != null && !contas.isEmpty()) {
			for(FatContasHospitalares conta : contas) {
				
				//-- Marina 16/12/2013 - Chamado 117625
				Short vQtdeRealizado = fatAtoMedicoAihDAO.obterQtdrealizada(conta.getSeq());
				Long cpf = null;
				Integer vIph = null;
				Short vIpho = null;
				BigDecimal vValorUti = null;
				AipPacientes paciente = null;

				FatEspelhoAih espelho = fatEspelhoAihDAO.obterPorChavePrimaria(new FatEspelhoAihId(conta.getSeq(), 1));
				if(espelho != null) {
					//Marina 03/01/2011 
					paciente = pacienteFacade.obterPacientePorProntuario(espelho.getPacProntuario());
					cpf = paciente != null ? paciente.getCpf() : null;
					vValorUti = obterAtendimentoEmergencia(conta.getSeq());
					
					VFatAssociacaoProcedimento assocProc = vFatAssociacaoProcedimentoDAO.buscaIphSms(conta.getPhiSeq(), Short.valueOf("12"));
					if(assocProc != null) {
						vIph = assocProc.getId().getIphSeq();
						vIpho = assocProc.getId().getIphPhoSeq();
					}
				
					Short vAno = espelho.getDciCpeAno();
					Integer contaReapresentadaSeq = conta.getContaHospitalarReapresentada() != null ? conta.getContaHospitalarReapresentada().getSeq() : 0;
					Integer codigoIBGE = zeroIntegerIfNull(espelho.getCodIbgeCidadePac());
					Short norSeqAih5 = zeroShortIfNull(espelho.getNroSeqaih5());
					String cnsResp = fatkCthnRN.aipcGetCnsResp(espelho.getCpfMedicoSolicRespons());
					
					inserirLinhaArqEspelho(espelho, zeroIntegerIfNull(conta.getSeq()), contaReapresentadaSeq, codigoIBGE, cadastroPacienteFacade.obterEndecoPaciente(paciente.getCodigo()), conta, 
							DateUtil.validaDataMaior(DateUtils.truncate(espelho.getDataInternacao(), Calendar.DAY_OF_MONTH),
									DateUtils.truncate(DateUtil.obterData(2011, 11, 1), Calendar.DAY_OF_MONTH)), norSeqAih5, paciente, 
							obterSenha(conta.getSeq(), vIph, vIpho, vValorUti), cnsResp, vQtdeRealizado, cpf, writer);
					
					inserirLinhaArqRealz(conta, vAno, espelho.getIphCodSusRealiz(), espelho.getIphCodSusSolic(), writer);
					
					popularServicos(conta, contaReapresentadaSeq, grupoOpm, vAno, writer);
	
					popularOpme(conta, contaReapresentadaSeq, grupoOpm, vAno, writer);
				}
			}
		}
	}
	
	private BigDecimal obterAtendimentoEmergencia(Integer cthSeq) {
		Object[] buscaUtiemerg = fatContasHospitalaresDAO.obterAtendimentoemergencia(cthSeq);
		if(buscaUtiemerg != null) {
			return (BigDecimal)buscaUtiemerg[1];
		}
		return null;
	}
	
	private String obterSenha(Integer cthSeq, Integer vIph, Short vIpho, BigDecimal vValorUti) {
		if(possuiCaracteristica(vIph, vIpho) || BigDecimal.ZERO.compareTo(vValorUti) < 0) {
			return faturamentoRN.buscaSenhaCerih(cthSeq);
		}
		return null;
	}

	private void inserirLinhaArqCabecalho(Date data, Short grupoOpm, ByteArrayOutputStream writer) throws IOException {
		Long vTotReg =  fatContasHospitalaresDAO.contaLaudoPGerarArquivoSms(data).longValueExact();
		Long vTotRegMp =  fatContasHospitalaresDAO.contaLaudoMPGerarArquivoSms(data).longValueExact();
		Long vTotRegS =  fatContasHospitalaresDAO.contaLaudSGerarArquivoSms(data, grupoOpm).longValueExact();

		vTotReg = vTotReg + vTotRegMp + vTotRegS;
		
		String cnpj = StringUtils.remove(aghuFacade.obterCgcHospital(), "-");
		cnpj = StringUtils.remove(cnpj, ".");
		cnpj = StringUtils.remove(cnpj, "/");
		IOUtils.write("02" + cnpj + StringUtils.leftPad(vTotReg.toString(), 4, '0') + System.getProperty(QUEBRA_LINHA), writer);
	}
	
	private void inserirLinhaArqRealz(FatContasHospitalares conta, Short vAno, Long vRealizado, Long vSolicitado, ByteArrayOutputStream writer) throws IOException {
		if(!vRealizado.equals(vSolicitado)) {
			if(conta.getContaHospitalarReapresentada() == null) {
				String linhaArquivo = "3" + vAno + "02" + StringUtils.leftPad(conta.getSeq().toString(), 6, '0') + 
						CoreUtil.calculaModuloOnze(Long.valueOf(vAno + "02" + StringUtils.leftPad(conta.getSeq().toString(), 6, '0'))) +
						StringUtils.leftPad(zeroIfNull(vRealizado), 10, '0') + "001" + "000000" + System.getProperty(QUEBRA_LINHA);
				IOUtils.write(linhaArquivo, writer);
			} else {
				String linhaArquivo = "3" + "2007" + "02" + StringUtils.leftPad(conta.getContaHospitalarReapresentada().getSeq().toString(), 6, '0') + 
						CoreUtil.calculaModuloOnze(Long.valueOf("2007" + "02" + StringUtils.leftPad(conta.getContaHospitalarReapresentada().getSeq().toString(), 6, '0'))) +
						StringUtils.leftPad(zeroIfNull(vRealizado), 10, '0') + "001" + "000000"+ System.getProperty(QUEBRA_LINHA);
				IOUtils.write(linhaArquivo, writer);
			}
		}
	}
	
	private void inserirLinhaArqEspelho(FatEspelhoAih espelho, Integer contaSeq, Integer contaReapresentadaSeq, Integer codigoIBGE, VAipEnderecoPaciente enderecoPaciente, 
			FatContasHospitalares conta, Boolean dataInternacaoPosterior2011, Short norSeqAih5, AipPacientes paciente, String vSenha, String cnsResp, Short vQtdeRealizado, 
			Long cpf, ByteArrayOutputStream writer) throws IOException {
		StringBuffer parte1 = new StringBuffer(200);
		StringBuffer parte2 = new StringBuffer(200);
		StringBuffer parte3 = new StringBuffer(200);
		StringBuffer parte4 = new StringBuffer(200);
		StringBuffer parte5 = new StringBuffer(200);
		StringBuffer parte6 = new StringBuffer(200);
		StringBuffer parte7 = new StringBuffer(200);
		
		obterParte1(parte1, espelho, contaSeq, contaReapresentadaSeq);
		obterParte2(parte2, espelho);
		obterParte3(parte3, espelho, codigoIBGE, enderecoPaciente);
		obterParte4(parte4, espelho, conta, dataInternacaoPosterior2011, norSeqAih5);
		obterParte5(parte5, espelho, paciente);
		obterParte6(parte6, espelho, norSeqAih5, contaReapresentadaSeq, vSenha);
		obterParte7(parte7, espelho, dataInternacaoPosterior2011, cnsResp, vQtdeRealizado, cpf);
		
		String linhaArquivo = parte1.toString() + parte2.toString() + parte3.toString() + parte4.toString() + parte5.toString() + parte6.toString() + parte7.toString() + System.getProperty(QUEBRA_LINHA);
		
		IOUtils.write(linhaArquivo, writer);
	}
	
	private Boolean isSomatorioMaiorZero(FatAtoMedicoAih servico) {
		BigDecimal somatorio = BigDecimal.ZERO.add((BigDecimal)CoreUtil.nvl(servico.getValorAnestesista(), BigDecimal.ZERO)).
				add((BigDecimal)CoreUtil.nvl(servico.getValorProcedimento(), BigDecimal.ZERO)).
				add((BigDecimal)CoreUtil.nvl(servico.getValorSadt(), BigDecimal.ZERO)).
				add((BigDecimal)CoreUtil.nvl(servico.getValorServHosp(), BigDecimal.ZERO)).
				add((BigDecimal)CoreUtil.nvl(servico.getValorServProf(), BigDecimal.ZERO));
		
		return somatorio.compareTo(BigDecimal.ZERO) > 0;
	}
	
	private String competenciaUti(FatContasHospitalares conta, Boolean competenciaUti, FatAtoMedicoAih ato) {
		return competenciaUti ? DateUtil.dataToString(conta.getDataInternacaoAdministrativa(), "MMyyyy") : 
			StringUtils.leftPad(ato.getQuantidade() == null ? "0" : ( StringUtils.substring(ato.getCompetenciaUti(), 4, 6) +  StringUtils.substring(ato.getCompetenciaUti(), 0, 4)), 6, '0');
	}
	
	private Integer calculaModuloAno(Boolean contaReaprIsZero, Short vAno, String linha) {
		return CoreUtil.calculaModuloOnze(Long.valueOf((contaReaprIsZero ? vAno : 2007) + "02" + linha));
	}
	
	private void popularServicos(FatContasHospitalares conta, Integer contaReapresentadaSeq, Short grupoOpm, Short vAno, ByteArrayOutputStream writer) 
			throws ApplicationBusinessException, IOException {
		List<FatAtoMedicoAih> servicos = fatAtoMedicoAihDAO.listarServicosProf(conta.getSeq(), grupoOpm);
		if(servicos != null && !servicos.isEmpty()) {
			Boolean contaReaprIsZero = contaReapresentadaSeq.equals(0);
			for(FatAtoMedicoAih servico : servicos) {
				
				if(isSomatorioMaiorZero(servico)) {
					Boolean competenciaUti = StringUtils.equalsIgnoreCase(servico.getCompetenciaUti(), "      ");
					String linha = contaReaprIsZero ? StringUtils.leftPad(servico.getId().getEaiCthSeq().toString(), 6, '0') : StringUtils.leftPad(contaReapresentadaSeq.toString(), 6, '0');
					
					StringBuffer servicoArq = new StringBuffer(200);
					servicoArq.append(fatcBuscaInstrReg(servico.getItemProcedimentoHospitalar().getId().getPhoSeq(), servico.getItemProcedimentoHospitalar().getId().getSeq()) ? "2" : "4")
					.append(contaReaprIsZero ? vAno : 2007)
					.append("02")
					.append(linha)
					.append(calculaModuloAno(contaReaprIsZero, vAno, linha))
					.append(StringUtils.leftPad(zeroIfNull(servico.getIphCodSus()), 10, '0'))
					.append(StringUtils.leftPad(zeroIfNull(servico.getQuantidade()), 3, '0'))
					.append(competenciaUti(conta, competenciaUti, servico))
					.append(System.getProperty(QUEBRA_LINHA));
					IOUtils.write(servicoArq.toString(), writer);
				}
			}
		}
	}

	private void popularOpme(FatContasHospitalares conta, Integer contaReapresentadaSeq, Short grupoOpm, Short vAno, ByteArrayOutputStream writer) 
			throws ApplicationBusinessException, IOException {
		List<FatAtoMedicoAih> opmes = fatAtoMedicoAihDAO.listarOpme(conta.getSeq(), grupoOpm);
		if(opmes != null && !opmes.isEmpty()) {
			Boolean contaReaprIsZero = contaReapresentadaSeq.equals(0);
			for(FatAtoMedicoAih opme : opmes) {
				Boolean competenciaUti = StringUtils.equalsIgnoreCase(opme.getCompetenciaUti(), "      ");
				String linha = contaReaprIsZero ? StringUtils.leftPad(opme.getId().getEaiCthSeq().toString(), 6, '0') : StringUtils.leftPad(contaReapresentadaSeq.toString(), 6, '0');
				
				StringBuffer opmeArq = new StringBuffer(200);
				opmeArq.append('5')
				.append(contaReaprIsZero ? vAno : 2007)
				.append("02")
				.append(linha)
				.append(calculaModuloAno(contaReaprIsZero, vAno, linha))
				.append(StringUtils.leftPad(zeroIfNull(opme.getIphCodSus()), 10, '0'))
				.append(StringUtils.leftPad(zeroIfNull(opme.getQuantidade()), 3, '0'))
				.append(competenciaUti(conta, competenciaUti, opme))
				.append(StringUtils.rightPad(" ", 20, " "))
				.append(System.getProperty(QUEBRA_LINHA));
				IOUtils.write(opmeArq.toString(), writer);
			}
		}
	}

	private void obterParte1(StringBuffer parte1, FatEspelhoAih espelho, Integer contaSeq, Integer contaReapresentadaSeq) {
		String modulo11 = (contaReapresentadaSeq.equals(0) ? espelho.getDciCpeAno() : 2007) + "02" + 
				(contaReapresentadaSeq.equals(0) ? StringUtils.leftPad(contaSeq.toString(), 6, '0') : StringUtils.leftPad(contaReapresentadaSeq.toString(), 6, '0'));
		parte1
		.append('0')
		.append(contaReapresentadaSeq.equals(0) ? espelho.getDciCpeAno() : 2007)
		.append("02")
		.append(contaReapresentadaSeq.equals(0) ? StringUtils.leftPad(contaSeq.toString(), 6, '0') : StringUtils.leftPad(contaReapresentadaSeq.toString(), 6, '0'))
		.append(CoreUtil.calculaModuloOnze(Long.valueOf(modulo11)))
		.append(StringUtils.rightPad(espelho.getPacNome(), 50));
	}

	private void obterParte2(StringBuffer parte2, FatEspelhoAih espelho) {
		parte2.append(espelho.getPacSexo().equals("1") ? "M" : "F")
		.append(StringUtils.leftPad(espelho.getPacDtNascimento() == null ? " " : DateUtil.dataToString(espelho.getPacDtNascimento(), ANO_MES_DIA), 8, ' '))
		.append(StringUtils.rightPad(espelho.getPacProntuario() == null ? "0" : espelho.getPacProntuario().toString(), 20, ' '))
		.append(StringUtils.rightPad(espelho.getEndLogradouroPac() == null ? " " : espelho.getEndLogradouroPac(), 47, ' '));
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void obterParte3(StringBuffer parte3, FatEspelhoAih espelho, Integer codigoIBGE, VAipEnderecoPaciente enderecoPaciente) {
		parte3.append(StringUtils.leftPad(espelho.getEndNroLogradouroPac() == null ? "0" : espelho.getEndNroLogradouroPac().toString(), 7, '0'))
		.append(StringUtils.rightPad(espelho.getEndCmplLogradouroPac() == null ? " " : espelho.getEndCmplLogradouroPac(), 20, ' '))
		.append(StringUtils.rightPad(enderecoPaciente != null ? (enderecoPaciente.getBairro() == null ? "NAO INFORMADO" : enderecoPaciente.getBairro()) : "NAO INFORMADO", 20, ' '))
		.append(StringUtils.leftPad(codigoIBGE != null ? calculaModuloDez(codigoIBGE).toString() : "0", 7, '0'));
	}

	private void obterParte4(StringBuffer parte4, FatEspelhoAih espelho, FatContasHospitalares conta, Boolean dataInternacaoPosterior2011, Short norSeqAih5) {
		parte4.append(StringUtils.rightPad(spaceIfNull(espelho.getEndUfPac()), 2, ' '))
		.append(StringUtils.leftPad(zeroIfNull(espelho.getEndCepPac()), 8, '0'))
		.append(norSeqAih5.equals(Short.valueOf("0")) ? StringUtils.leftPad(espelho.getDataInternacao() == null ? "0" : DateUtil.dataToString(espelho.getDataInternacao(), ANO_MES_DIA), 8, '0') : 
			StringUtils.leftPad("0", 8, '0'))
		.append(StringUtils.leftPad(espelho.getDataSaida() == null ? "0" : DateUtil.dataToString(espelho.getDataSaida(), ANO_MES_DIA), 8, '0'))
		.append(obterMotivoSaidaPaciente(conta))
		.append(!dataInternacaoPosterior2011 ? StringUtils.leftPad(espelho.getDataInternacao() == null ? "0" : espelho.getCpfMedicoSolicRespons().toString(), 11, '0') : 
			StringUtils.leftPad("0", 11, '0'));
	}

	private void obterParte5(StringBuffer parte5, FatEspelhoAih espelho, AipPacientes paciente) {
		parte5.append(StringUtils.leftPad(zeroIfNull(espelho.getIphCodSusSolic()), 10, '0'))
		.append(StringUtils.leftPad(zeroIfNull(espelho.getTciCodSus()), 2, '0'))
		.append(espelho.getEspecialidadeAih() == null ? "1" : StringUtils.substring(espelho.getEspecialidadeAih().toString(), 0, 1))
		.append(StringUtils.leftPad(zeroIfNull(paciente.getNroCartaoSaude()), 17, '0'))
		.append(StringUtils.leftPad(zeroIfNull(espelho.getMotivoCobranca()), 2, '0'));
	}

	private void obterParte6(StringBuffer parte6, FatEspelhoAih espelho, Short norSeqAih5, Integer contaReapresentadaSeq, String vSenha) {
		parte6.append(StringUtils.leftPad(zeroIfNull(espelho.getNumeroAih()), 13, '0'))
		.append(StringUtils.leftPad(zeroIfNull(espelho.getNroSeqaih5()), 3, '0'))
		.append(contaReapresentadaSeq.equals(0) ? ( norSeqAih5.equals(Short.valueOf("0")) ? "0" : "5" ) : "9")
		.append(StringUtils.leftPad(zeroIfNull(espelho.getNroSisprenatal()), 15, '0'))
		.append(StringUtils.leftPad(espelho.getDataSaida() == null ? "0" : DateUtil.dataToString(espelho.getDataSaida(), ANO_MES_DIA), 8, '0'))
		.append(StringUtils.leftPad(zeroIfNull(vSenha), 15, '0'))
		.append(StringUtils.rightPad(espelho.getCidPrimario() == null ? " " : espelho.getCidPrimario().toString(), 4, ' '));
	}

	private void obterParte7(StringBuffer parte7, FatEspelhoAih espelho, Boolean dataInternacaoPosterior2011, String cnsResp, Short vQtdeRealizado, Long cpf) {
		parte7.append(StringUtils.leftPad(zeroIfNull(cpf), 11, '0'))
		.append(StringUtils.leftPad(zeroIfNull(espelho.getConCodCentral()), 9, '0'))
		.append("0000000000000")
		.append(dataInternacaoPosterior2011 ? StringUtils.leftPad("0", 15, '0') : 
			StringUtils.leftPad(zeroIfNull(cnsResp), 15, '0'))
		.append(StringUtils.leftPad(zeroIfNull(vQtdeRealizado.toString()), 4, '0'));
	}

	private Boolean fatcBuscaInstrReg(Short iphPhoSeq, Integer iphSeq) {
		final FatProcedimentoRegistro fatProcedimentoRegistro = fatProcedimentoRegistroDAO.buscarPrimeiroPorCodigosRegistroEPorIph(new String[]{"03"},
				iphPhoSeq, iphSeq);
		if (fatProcedimentoRegistro == null) {
			return false;
		}
		return true;
	}
		
	private Boolean possuiCaracteristica(Integer vIph, Short vIpho) {
		 List<FatCaractItemProcHosp> caracteristicas = fatCaractItemProcHospDAO.listarPorIphCaracteristica(vIph, vIpho, DominioFatTipoCaractItem.EXIGE_CERIH_INTERNACAO);
		 if(caracteristicas != null && !caracteristicas.isEmpty()) {
			 return caracteristicas.get(0).getValorChar() == null ? true : DominioSimNao.valueOf(caracteristicas.get(0).getValorChar()).isSim();
		 }
		 return false;
	}
	
	private String obterMotivoSaidaPaciente(FatContasHospitalares conta) {
		if(conta.getMotivoSaidaPaciente() != null) {
			if(conta.getMotivoSaidaPaciente().getSeq().equals(2)) {
				return "S";
			}
			if(conta.getMotivoSaidaPaciente().getSeq().equals(3)) {
				return "T";
			}
			if(conta.getMotivoSaidaPaciente().getSeq().equals(4) || conta.getMotivoSaidaPaciente().getSeq().equals(5)) {
				return "O";
			}
		}
		return "A";
	}

	private String calculaModuloDez(Object numero) {
        int sum = 0;
        int digit = 0;
        int addend = 0;
        boolean timesTwo = false;
        for (int i = numero.toString().length() - 1; i >= 0; i--)
        {
                digit = Integer.parseInt(numero.toString().substring(i, i + 1));
                if (timesTwo)
                {
                        addend = digit * 2;
                        if (addend > 9) {
                                addend -= 9;
                        }
                }
                else {
                        addend = digit;
                }
                sum += addend;
                timesTwo = !timesTwo;
        }
        int modulus = sum % 10;
        return numero.toString() + modulus;
	}
	
	private Short zeroShortIfNull(Short o) {
		if(o != null) {
			return o;
		}
		return Short.valueOf("0");
	}

	private Integer zeroIntegerIfNull(Integer o) {
		if(o != null) {
			return o;
		}
		return 0;
	}

	private String zeroIfNull(Object o) {
		if(o != null) {
			return o.toString();
		}
		return "0";
	}

	private String spaceIfNull(Object o) {
		if(o != null) {
			return o.toString();
		}
		return " ";
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}

